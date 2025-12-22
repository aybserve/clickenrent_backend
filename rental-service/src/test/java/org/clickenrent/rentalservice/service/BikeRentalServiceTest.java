package org.clickenrent.rentalservice.service;

import org.clickenrent.rentalservice.dto.BikeRentalDTO;
import org.clickenrent.rentalservice.entity.Bike;
import org.clickenrent.rentalservice.entity.BikeRental;
import org.clickenrent.rentalservice.entity.Rental;
import org.clickenrent.rentalservice.exception.ResourceNotFoundException;
import org.clickenrent.rentalservice.exception.UnauthorizedException;
import org.clickenrent.rentalservice.mapper.BikeRentalMapper;
import org.clickenrent.rentalservice.repository.BikeRentalRepository;
import org.clickenrent.rentalservice.repository.BikeRepository;
import org.clickenrent.rentalservice.repository.RentalRepository;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for BikeRentalService.
 */
@ExtendWith(MockitoExtension.class)
class BikeRentalServiceTest {

    @Mock
    private BikeRentalRepository bikeRentalRepository;

    @Mock
    private BikeRepository bikeRepository;

    @Mock
    private RentalRepository rentalRepository;

    @Mock
    private BikeRentalMapper bikeRentalMapper;

    @Mock
    private SecurityService securityService;


    @InjectMocks
    private BikeRentalService bikeRentalService;

    private BikeRental testBikeRental;
    private BikeRentalDTO testBikeRentalDTO;
    private Bike testBike;
    private Rental testRental;

    @BeforeEach
    void setUp() {
        testBike = Bike.builder()
        .id(1L)
        .code("BIKE001")
        .build();

        testRental = Rental.builder()
        .id(1L)
        .userExternalId("usr-ext-00001")
        .companyExternalId("company-ext-001")
        .build();

        testBikeRental = BikeRental.builder()
        .id(1L)
        .externalId("BR001")
        .rental(testRental)
        .bike(testBike)
        .startDateTime(LocalDateTime.now())
        .endDateTime(LocalDateTime.now().plusDays(1))
        .build();

        testBikeRentalDTO = BikeRentalDTO.builder()
        .id(1L)
        .externalId("BR001")
        .rentalId(1L)
        .bikeId(1L)
        .locationId(1L)
        .bikeRentalStatusId(1L)
        .rentalUnitId(1L)
        .startDateTime(LocalDateTime.now())
        .endDateTime(LocalDateTime.now().plusDays(1))
        .build();
    }

    @Test
    void getAllBikeRentals_WithAdminRole_ReturnsAllRentals() {
        // Arrange
        when(securityService.isAdmin()).thenReturn(true);
        Pageable pageable = PageRequest.of(0, 20);
        Page<BikeRental> rentalPage = new PageImpl<>(Collections.singletonList(testBikeRental));
        when(bikeRentalRepository.findAll(pageable)).thenReturn(rentalPage);
        when(bikeRentalMapper.toDto(testBikeRental)).thenReturn(testBikeRentalDTO);

        // Act
        Page<BikeRentalDTO> result = bikeRentalService.getAllBikeRentals(pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("BR001", result.getContent().get(0).getExternalId());
        verify(bikeRentalRepository, times(1)).findAll(pageable);
    }

    @Test
    void getAllBikeRentals_WithoutAdminRole_ThrowsUnauthorizedException() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 20);
        // Act & Assert
        assertThrows(UnauthorizedException.class, () -> bikeRentalService.getAllBikeRentals(pageable));
    }

    @Test
    void getBikeRentalById_WithAdminRole_Success() {
        // Arrange
        when(securityService.isAdmin()).thenReturn(true);
        when(bikeRentalRepository.findById(1L)).thenReturn(Optional.of(testBikeRental));
        when(bikeRentalMapper.toDto(testBikeRental)).thenReturn(testBikeRentalDTO);

        // Act
        BikeRentalDTO result = bikeRentalService.getBikeRentalById(1L);

        // Assert
        assertNotNull(result);
        assertEquals("BR001", result.getExternalId());
        verify(bikeRentalRepository, times(1)).findById(1L);
    }

    @Test
    void getBikeRentalById_NotFound() {
        // Arrange
        when(bikeRentalRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> bikeRentalService.getBikeRentalById(999L));
    }

    @Test
    void getBikeRentalById_WithoutAccess_ThrowsUnauthorizedException() {
        // Arrange
        when(bikeRentalRepository.findById(1L)).thenReturn(Optional.of(testBikeRental));
        when(securityService.isAdmin()).thenReturn(false);
        // Act & Assert
        assertThrows(UnauthorizedException.class, () -> bikeRentalService.getBikeRentalById(1L));
    }

    @Test
    void createBikeRental_Success() {
        // Arrange
        when(securityService.hasAccessToUser(anyLong())).thenReturn(true);
        when(bikeRepository.findById(1L)).thenReturn(Optional.of(testBike));
        when(rentalRepository.findById(1L)).thenReturn(Optional.of(testRental));
        when(bikeRentalMapper.toEntity(testBikeRentalDTO)).thenReturn(testBikeRental);
        when(bikeRentalRepository.save(any(BikeRental.class))).thenReturn(testBikeRental);
        when(bikeRentalMapper.toDto(testBikeRental)).thenReturn(testBikeRentalDTO);

        // Act
        BikeRentalDTO result = bikeRentalService.createBikeRental(testBikeRentalDTO);

        // Assert
        assertNotNull(result);
        assertEquals("BR001", result.getExternalId());
        verify(bikeRentalRepository, times(1)).save(any(BikeRental.class));
    }

    @Test
    void createBikeRental_BikeNotFound() {
        // Arrange
        when(bikeRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> bikeRentalService.createBikeRental(testBikeRentalDTO));
    }

    @Test
    void createBikeRental_RentalNotFound() {
        // Arrange
        when(bikeRepository.findById(1L)).thenReturn(Optional.of(testBike));
        when(rentalRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> bikeRentalService.createBikeRental(testBikeRentalDTO));
    }

    @Test
    void deleteBikeRental_WithAdminRole_Success() {
        // Arrange
        when(securityService.isAdmin()).thenReturn(true);
        when(bikeRentalRepository.findById(1L)).thenReturn(Optional.of(testBikeRental));
        doNothing().when(bikeRentalRepository).delete(testBikeRental);

        // Act
        bikeRentalService.deleteBikeRental(1L);

        // Assert
        verify(bikeRentalRepository, times(1)).findById(1L);
        verify(bikeRentalRepository, times(1)).delete(testBikeRental);
    }

    @Test
    void deleteBikeRental_WithoutAdminRole_ThrowsUnauthorizedException() {
        // Arrange
        when(bikeRentalRepository.findById(1L)).thenReturn(Optional.of(testBikeRental));
        // Act & Assert
        assertThrows(UnauthorizedException.class, () -> bikeRentalService.deleteBikeRental(1L));
        verify(bikeRentalRepository, never()).delete(any(BikeRental.class));
    }

    @Test
    void deleteBikeRental_NotFound() {
        // Arrange
        when(bikeRentalRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> bikeRentalService.deleteBikeRental(999L));
        verify(bikeRentalRepository, never()).delete(any(BikeRental.class));
    }
}
