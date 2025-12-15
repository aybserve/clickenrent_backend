package org.clickenrent.rentalservice.service;

import org.clickenrent.rentalservice.dto.LocationDTO;
import org.clickenrent.rentalservice.entity.Hub;
import org.clickenrent.rentalservice.entity.Location;
import org.clickenrent.rentalservice.exception.ResourceNotFoundException;
import org.clickenrent.rentalservice.exception.UnauthorizedException;
import org.clickenrent.rentalservice.mapper.LocationMapper;
import org.clickenrent.rentalservice.repository.HubRepository;
import org.clickenrent.rentalservice.repository.LocationRepository;
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

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for LocationService.
 */
@ExtendWith(MockitoExtension.class)
class LocationServiceTest {

    @Mock
    private LocationRepository locationRepository;

    @Mock
    private HubRepository hubRepository;

    @Mock
    private LocationMapper locationMapper;

    @Mock
    private SecurityService securityService;

    @InjectMocks
    private LocationService locationService;

    private Location testLocation;
    private LocationDTO testLocationDTO;

    @BeforeEach
    void setUp() {
        testLocation = Location.builder()
                .id(1L)
                .externalId("LOC001")
                .name("Amsterdam Central")
                .address("Stationsplein 1, Amsterdam")
                .companyId(1L)
                .isPublic(true)
                .build();

        testLocationDTO = LocationDTO.builder()
                .id(1L)
                .externalId("LOC001")
                .name("Amsterdam Central")
                .address("Stationsplein 1, Amsterdam")
                .companyId(1L)
                .isPublic(true)
                .build();
    }

    @Test
    void getAllLocations_WithAdminRole_ReturnsAllLocations() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 20);
        Page<Location> locationPage = new PageImpl<>(Collections.singletonList(testLocation));
        when(securityService.isAdmin()).thenReturn(true);
        when(locationRepository.findAll(pageable)).thenReturn(locationPage);
        when(locationMapper.toDto(testLocation)).thenReturn(testLocationDTO);

        // Act
        Page<LocationDTO> result = locationService.getAllLocations(pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Amsterdam Central", result.getContent().get(0).getName());
        verify(locationRepository, times(1)).findAll(pageable);
    }

    @Test
    void getAllLocations_WithoutAdminRole_ThrowsUnauthorizedException() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 20);
        when(securityService.isAdmin()).thenReturn(false);

        // Act & Assert
        assertThrows(UnauthorizedException.class, () -> locationService.getAllLocations(pageable));
    }

    @Test
    void getLocationById_WithAdminRole_Success() {
        // Arrange
        when(locationRepository.findById(1L)).thenReturn(Optional.of(testLocation));
        when(securityService.isAdmin()).thenReturn(true);
        when(locationMapper.toDto(testLocation)).thenReturn(testLocationDTO);

        // Act
        LocationDTO result = locationService.getLocationById(1L);

        // Assert
        assertNotNull(result);
        assertEquals("Amsterdam Central", result.getName());
        verify(locationRepository, times(1)).findById(1L);
    }

    @Test
    void getLocationById_NotFound() {
        // Arrange
        when(locationRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> locationService.getLocationById(999L));
    }

    @Test
    void getLocationById_WithoutAccess_ThrowsUnauthorizedException() {
        // Arrange
        when(locationRepository.findById(1L)).thenReturn(Optional.of(testLocation));
        when(securityService.isAdmin()).thenReturn(false);
        when(securityService.hasAccessToCompany(1L)).thenReturn(false);

        // Act & Assert
        assertThrows(UnauthorizedException.class, () -> locationService.getLocationById(1L));
    }

    @Test
    void getLocationByExternalId_Success() {
        // Arrange
        when(locationRepository.findByExternalId("LOC001")).thenReturn(Optional.of(testLocation));
        when(locationMapper.toDto(testLocation)).thenReturn(testLocationDTO);

        // Act
        LocationDTO result = locationService.getLocationByExternalId("LOC001");

        // Assert
        assertNotNull(result);
        assertEquals("LOC001", result.getExternalId());
        verify(locationRepository, times(1)).findByExternalId("LOC001");
    }

    @Test
    void getLocationByExternalId_NotFound() {
        // Arrange
        when(locationRepository.findByExternalId("NONEXISTENT")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> locationService.getLocationByExternalId("NONEXISTENT"));
    }

    @Test
    void createLocation_WithAdminRole_CreatesLocationAndHub() {
        // Arrange
        when(securityService.isAdmin()).thenReturn(true);
        when(locationMapper.toEntity(testLocationDTO)).thenReturn(testLocation);
        when(locationRepository.save(any(Location.class))).thenReturn(testLocation);
        when(hubRepository.save(any(Hub.class))).thenReturn(new Hub());
        when(locationMapper.toDto(testLocation)).thenReturn(testLocationDTO);

        // Act
        LocationDTO result = locationService.createLocation(testLocationDTO);

        // Assert
        assertNotNull(result);
        assertEquals("Amsterdam Central", result.getName());
        verify(locationRepository, times(1)).save(any(Location.class));
        verify(hubRepository, times(1)).save(any(Hub.class)); // Verify hub was created
    }

    @Test
    void createLocation_WithCompanyAccess_Success() {
        // Arrange
        when(securityService.isAdmin()).thenReturn(false);
        when(securityService.hasAccessToCompany(1L)).thenReturn(true);
        when(locationMapper.toEntity(testLocationDTO)).thenReturn(testLocation);
        when(locationRepository.save(any(Location.class))).thenReturn(testLocation);
        when(hubRepository.save(any(Hub.class))).thenReturn(new Hub());
        when(locationMapper.toDto(testLocation)).thenReturn(testLocationDTO);

        // Act
        LocationDTO result = locationService.createLocation(testLocationDTO);

        // Assert
        assertNotNull(result);
        verify(locationRepository, times(1)).save(any(Location.class));
        verify(hubRepository, times(1)).save(any(Hub.class));
    }

    @Test
    void createLocation_WithoutAccess_ThrowsUnauthorizedException() {
        // Arrange
        when(securityService.isAdmin()).thenReturn(false);
        when(securityService.hasAccessToCompany(1L)).thenReturn(false);

        // Act & Assert
        assertThrows(UnauthorizedException.class, () -> locationService.createLocation(testLocationDTO));
        verify(locationRepository, never()).save(any(Location.class));
    }

    @Test
    void updateLocation_WithAdminRole_Success() {
        // Arrange
        when(locationRepository.findById(1L)).thenReturn(Optional.of(testLocation));
        when(securityService.isAdmin()).thenReturn(true);
        when(locationRepository.save(any(Location.class))).thenReturn(testLocation);
        when(locationMapper.toDto(testLocation)).thenReturn(testLocationDTO);

        // Act
        LocationDTO result = locationService.updateLocation(1L, testLocationDTO);

        // Assert
        assertNotNull(result);
        verify(locationRepository, times(1)).findById(1L);
        verify(locationRepository, times(1)).save(any(Location.class));
    }

    @Test
    void updateLocation_NotFound() {
        // Arrange
        when(locationRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> locationService.updateLocation(999L, testLocationDTO));
    }

    @Test
    void deleteLocation_WithAdminRole_Success() {
        // Arrange
        when(locationRepository.findById(1L)).thenReturn(Optional.of(testLocation));
        when(securityService.isAdmin()).thenReturn(true);
        doNothing().when(locationRepository).delete(testLocation);

        // Act
        locationService.deleteLocation(1L);

        // Assert
        verify(locationRepository, times(1)).findById(1L);
        verify(locationRepository, times(1)).delete(testLocation);
    }

    @Test
    void deleteLocation_WithoutAdminRole_ThrowsUnauthorizedException() {
        // Arrange
        when(locationRepository.findById(1L)).thenReturn(Optional.of(testLocation));
        when(securityService.isAdmin()).thenReturn(false);

        // Act & Assert
        assertThrows(UnauthorizedException.class, () -> locationService.deleteLocation(1L));
        verify(locationRepository, never()).delete(any(Location.class));
    }

    @Test
    void deleteLocation_NotFound() {
        // Arrange
        when(locationRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> locationService.deleteLocation(999L));
        verify(locationRepository, never()).delete(any(Location.class));
    }
}
