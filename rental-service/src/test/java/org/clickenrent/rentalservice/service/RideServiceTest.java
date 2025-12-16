package org.clickenrent.rentalservice.service;

import org.clickenrent.rentalservice.dto.RideDTO;
import org.clickenrent.rentalservice.entity.BikeRental;
import org.clickenrent.rentalservice.entity.Rental;
import org.clickenrent.rentalservice.entity.Ride;
import org.clickenrent.rentalservice.entity.RideStatus;
import org.clickenrent.rentalservice.exception.ResourceNotFoundException;
import org.clickenrent.rentalservice.mapper.RideMapper;
import org.clickenrent.rentalservice.repository.BikeRentalRepository;
import org.clickenrent.rentalservice.repository.RideRepository;
import org.clickenrent.rentalservice.repository.RideStatusRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RideServiceTest {

    @Mock
    private RideRepository rideRepository;

    @Mock
    private BikeRentalRepository bikeRentalRepository;

    @Mock
    private RideStatusRepository rideStatusRepository;

    @Mock
    private RideMapper rideMapper;

    @Mock
    private SecurityService securityService;

    @InjectMocks
    private RideService rideService;

    private Ride testRide;
    private RideDTO testRideDTO;

    @BeforeEach
    void setUp() {
        testRide = Ride.builder()
        .id(1L)
        .externalId("RIDE001")
        .startDateTime(LocalDateTime.now())
        .endDateTime(null)
        .build();

        testRideDTO = RideDTO.builder()
        .id(1L)
        .externalId("RIDE001")
        .bikeRentalId(1L)
        .startDateTime(LocalDateTime.now())
        .endDateTime(null)
        .startLocationId(1L)
        .endLocationId(2L)
        .coordinatesId(1L)
        .rideStatusId(1L)
        .build();
        
    }

    @Test
    void getAllRides_ReturnsAllRides() {
        when(securityService.isAdmin()).thenReturn(true);
        Pageable pageable = PageRequest.of(0, 20);
        Page<Ride> ridePage = new PageImpl<>(Collections.singletonList(testRide));
        when(rideRepository.findAll(pageable)).thenReturn(ridePage);
        when(rideMapper.toDto(testRide)).thenReturn(testRideDTO);

        Page<RideDTO> result = rideService.getAllRides(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(rideRepository, times(1)).findAll(pageable);
    }

    @Test
    void getRideById_Success() {
        when(securityService.isAdmin()).thenReturn(true);
        BikeRental bikeRental = new BikeRental();
        Rental rental = new Rental();
        rental.setUserId(1L);
        bikeRental.setRental(rental);
        testRide.setBikeRental(bikeRental);
        
        when(rideRepository.findById(1L)).thenReturn(Optional.of(testRide));
        when(rideMapper.toDto(testRide)).thenReturn(testRideDTO);

        RideDTO result = rideService.getRideById(1L);

        assertNotNull(result);
        assertEquals("RIDE001", result.getExternalId());
        verify(rideRepository, times(1)).findById(1L);
    }

    @Test
    void getRideById_NotFound() {
        when(rideRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> rideService.getRideById(999L));
    }

    @Test
    void getRidesByBikeRental_Success() {
        when(securityService.isAdmin()).thenReturn(true);
        BikeRental bikeRental = new BikeRental();
        Rental rental = new Rental();
        rental.setUserId(1L);
        bikeRental.setRental(rental);
        
        when(bikeRentalRepository.findById(1L)).thenReturn(Optional.of(bikeRental));
        when(rideRepository.findByBikeRental(bikeRental)).thenReturn(Collections.singletonList(testRide));
        when(rideMapper.toDto(testRide)).thenReturn(testRideDTO);

        var result = rideService.getRidesByBikeRental(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(rideRepository, times(1)).findByBikeRental(bikeRental);
    }

    @Test
    void startRide_Success() {
        when(securityService.isAdmin()).thenReturn(true);
        BikeRental bikeRental = new BikeRental();
        Rental rental = new Rental();
        rental.setUserId(1L);
        bikeRental.setRental(rental);
        
        RideStatus activeStatus = new RideStatus();
        activeStatus.setName("Active");
        
        when(bikeRentalRepository.findById(1L)).thenReturn(Optional.of(bikeRental));
        when(rideMapper.toEntity(testRideDTO)).thenReturn(testRide);
        when(rideStatusRepository.findByName("Active")).thenReturn(Optional.of(activeStatus));
        when(rideRepository.save(any())).thenReturn(testRide);
        when(rideMapper.toDto(testRide)).thenReturn(testRideDTO);

        RideDTO result = rideService.startRide(testRideDTO);

        assertNotNull(result);
        verify(rideRepository, times(1)).save(any());
    }

    @Test
    void endRide_Success() {
        when(securityService.isAdmin()).thenReturn(true);
        BikeRental bikeRental = new BikeRental();
        Rental rental = new Rental();
        rental.setUserId(1L);
        bikeRental.setRental(rental);
        testRide.setBikeRental(bikeRental);
        
        RideStatus finishedStatus = new RideStatus();
        finishedStatus.setName("Finished");
        
        when(rideRepository.findById(1L)).thenReturn(Optional.of(testRide));
        when(rideStatusRepository.findByName("Finished")).thenReturn(Optional.of(finishedStatus));
        when(rideRepository.save(any())).thenReturn(testRide);
        when(rideMapper.toDto(testRide)).thenReturn(testRideDTO);

        RideDTO result = rideService.endRide(1L, testRideDTO);

        assertNotNull(result);
        verify(rideRepository, times(1)).save(any());
    }

    @Test
    void deleteRide_Success() {
        when(securityService.isAdmin()).thenReturn(true);
        when(rideRepository.findById(1L)).thenReturn(Optional.of(testRide));
        doNothing().when(rideRepository).delete(testRide);

        rideService.deleteRide(1L);

        verify(rideRepository, times(1)).delete(testRide);
    }

    @Test
    void deleteRide_NotFound() {
        when(rideRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> rideService.deleteRide(999L));
    }
}
