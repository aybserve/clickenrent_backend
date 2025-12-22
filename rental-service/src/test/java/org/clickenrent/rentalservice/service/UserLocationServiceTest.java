package org.clickenrent.rentalservice.service;

import org.clickenrent.rentalservice.dto.UserLocationDTO;
import org.clickenrent.rentalservice.entity.Location;
import org.clickenrent.rentalservice.entity.UserLocation;
import org.clickenrent.rentalservice.exception.ResourceNotFoundException;
import org.clickenrent.rentalservice.mapper.UserLocationMapper;
import org.clickenrent.rentalservice.repository.LocationRepository;
import org.clickenrent.rentalservice.repository.UserLocationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserLocationServiceTest {

    @Mock
    private UserLocationRepository userLocationRepository;

    @Mock
    private LocationRepository locationRepository;

    @Mock
    private UserLocationMapper userLocationMapper;

    @Mock
    private SecurityService securityService;

    @InjectMocks
    private UserLocationService userLocationService;

    private UserLocation testUserLocation;
    private UserLocationDTO testUserLocationDTO;
    private Location testLocation;

    @BeforeEach
    void setUp() {
        testLocation = Location.builder()
        .id(1L)
        .companyExternalId("company-ext-001")
        .build();

        testUserLocation = UserLocation.builder()
        .id(1L)
        .userExternalId("usr-ext-00001")
        .location(testLocation)
        .build();

        testUserLocationDTO = UserLocationDTO.builder()
        .id(1L)
        .userExternalId("usr-ext-00001")
        .locationId(1L)
        .locationRoleId(1L)
        .build();

    }

    @Test
    void getUserLocationsByUser_Success() {
        when(securityService.isAdmin()).thenReturn(true);
        when(userLocationRepository.findByUserExternalId("usr-ext-00001")).thenReturn(Collections.singletonList(testUserLocation));
        when(userLocationMapper.toDto(testUserLocation)).thenReturn(testUserLocationDTO);

        List<UserLocationDTO> result = userLocationService.getUserLocationsByUserExternalId("usr-ext-00001");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("usr-ext-00001", result.get(0).getUserExternalId());
        verify(userLocationRepository, times(1)).findByUserExternalId("usr-ext-00001");
    }

    @Test
    void getUserLocationsByLocation_Success() {
        when(securityService.isAdmin()).thenReturn(true);
        when(locationRepository.findById(1L)).thenReturn(Optional.of(testLocation));
        when(userLocationRepository.findByLocation(testLocation)).thenReturn(Collections.singletonList(testUserLocation));
        when(userLocationMapper.toDto(testUserLocation)).thenReturn(testUserLocationDTO);

        List<UserLocationDTO> result = userLocationService.getUserLocationsByLocation(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getLocationId());
        verify(locationRepository, times(1)).findById(1L);
    }

    @Test
    void assignUserToLocation_Success() {
        when(securityService.isAdmin()).thenReturn(true);
        when(locationRepository.findById(1L)).thenReturn(Optional.of(testLocation));
        when(userLocationMapper.toEntity(testUserLocationDTO)).thenReturn(testUserLocation);
        when(userLocationRepository.save(any())).thenReturn(testUserLocation);
        when(userLocationMapper.toDto(testUserLocation)).thenReturn(testUserLocationDTO);

        UserLocationDTO result = userLocationService.assignUserToLocation(testUserLocationDTO);

        assertNotNull(result);
        verify(userLocationRepository, times(1)).save(any());
    }

    @Test
    void removeUserFromLocation_Success() {
        when(securityService.isAdmin()).thenReturn(true);
        when(userLocationRepository.findById(1L)).thenReturn(Optional.of(testUserLocation));
        doNothing().when(userLocationRepository).delete(testUserLocation);

        userLocationService.removeUserFromLocation(1L);

        verify(userLocationRepository, times(1)).delete(testUserLocation);
    }

    @Test
    void removeUserFromLocation_NotFound() {
        when(userLocationRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userLocationService.removeUserFromLocation(999L));
    }
}




