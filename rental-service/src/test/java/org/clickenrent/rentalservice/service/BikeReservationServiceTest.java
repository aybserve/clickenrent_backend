package org.clickenrent.rentalservice.service;

import org.clickenrent.rentalservice.dto.BikeReservationDTO;
import org.clickenrent.rentalservice.entity.Bike;
import org.clickenrent.rentalservice.entity.BikeReservation;
import org.clickenrent.rentalservice.exception.ResourceNotFoundException;
import org.clickenrent.rentalservice.exception.UnauthorizedException;
import org.clickenrent.rentalservice.mapper.BikeReservationMapper;
import org.clickenrent.rentalservice.repository.BikeRepository;
import org.clickenrent.rentalservice.repository.BikeReservationRepository;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for BikeReservationService.
 */
@ExtendWith(MockitoExtension.class)
class BikeReservationServiceTest {

    @Mock
    private BikeReservationRepository bikeReservationRepository;

    @Mock
    private BikeRepository bikeRepository;

    @Mock
    private BikeReservationMapper bikeReservationMapper;

    @Mock
    private SecurityService securityService;


    @InjectMocks
    private BikeReservationService bikeReservationService;

    private BikeReservation testReservation;
    private BikeReservationDTO testReservationDTO;
    private Bike testBike;

    @BeforeEach
    void setUp() {
        testBike = Bike.builder()
        .id(1L)
        .code("BIKE001")
        .build();

        testReservation = BikeReservation.builder()
        .id(1L)
        .externalId("BRES001")
        .userExternalId("usr-ext-00001")
        .bike(testBike)
        .startDateTime(LocalDateTime.now().plusDays(1))
        .endDateTime(LocalDateTime.now().plusDays(2))
        .build();

        testReservationDTO = BikeReservationDTO.builder()
        .id(1L)
        .externalId("BRES001")
        .userExternalId("usr-ext-00001")
        .bikeId(1L)
        .startDateTime(LocalDateTime.now().plusDays(1))
        .endDateTime(LocalDateTime.now().plusDays(2))
        .build();
    }

    @Test
    void getAllReservations_WithAdminRole_ReturnsAllReservations() {
        // Arrange
        when(securityService.isAdmin()).thenReturn(true);
        Pageable pageable = PageRequest.of(0, 20);
        Page<BikeReservation> reservationPage = new PageImpl<>(Collections.singletonList(testReservation));
        when(bikeReservationRepository.findAll(pageable)).thenReturn(reservationPage);
        when(bikeReservationMapper.toDto(testReservation)).thenReturn(testReservationDTO);

        // Act
        Page<BikeReservationDTO> result = bikeReservationService.getAllReservations(pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(bikeReservationRepository, times(1)).findAll(pageable);
    }

    @Test
    void getAllReservations_WithoutAdminRole_ThrowsUnauthorizedException() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 20);
        // Act & Assert
        assertThrows(UnauthorizedException.class, () -> bikeReservationService.getAllReservations(pageable));
    }

    @Test
    void getReservationsByUser_WithAdminRole_Success() {
        // Arrange
        when(securityService.isAdmin()).thenReturn(true);
        when(bikeReservationRepository.findByUserExternalId("usr-ext-00001")).thenReturn(Arrays.asList(testReservation));
        when(bikeReservationMapper.toDto(testReservation)).thenReturn(testReservationDTO);

        // Act
        var result = bikeReservationService.getReservationsByUserExternalId("usr-ext-00001");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(bikeReservationRepository, times(1)).findByUserExternalId("usr-ext-00001");
    }

    @Test
    void getReservationsByUser_WithAccessToUser_Success() {
        // Arrange
        when(securityService.isAdmin()).thenReturn(false);
        when(securityService.hasAccessToUserByExternalId("usr-ext-00001")).thenReturn(true);
        when(bikeReservationRepository.findByUserExternalId("usr-ext-00001")).thenReturn(Arrays.asList(testReservation));
        when(bikeReservationMapper.toDto(testReservation)).thenReturn(testReservationDTO);

        // Act
        var result = bikeReservationService.getReservationsByUserExternalId("usr-ext-00001");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getReservationsByUser_WithoutAccess_ThrowsUnauthorizedException() {
        // Arrange
        when(securityService.isAdmin()).thenReturn(false);
        when(securityService.hasAccessToUserByExternalId("usr-ext-00001")).thenReturn(false);
        // Act & Assert
        assertThrows(UnauthorizedException.class, () -> bikeReservationService.getReservationsByUserExternalId("usr-ext-00001"));
    }

    @Test
    void getReservationById_WithAdminRole_Success() {
        // Arrange
        when(securityService.isAdmin()).thenReturn(true);
        when(bikeReservationRepository.findById(1L)).thenReturn(Optional.of(testReservation));
        when(bikeReservationMapper.toDto(testReservation)).thenReturn(testReservationDTO);

        // Act
        BikeReservationDTO result = bikeReservationService.getReservationById(1L);

        // Assert
        assertNotNull(result);
        assertEquals("BRES001", result.getExternalId());
        verify(bikeReservationRepository, times(1)).findById(1L);
    }

    @Test
    void getReservationById_NotFound() {
        // Arrange
        when(bikeReservationRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> bikeReservationService.getReservationById(999L));
    }

    @Test
    void getReservationById_WithoutAccess_ThrowsUnauthorizedException() {
        // Arrange
        when(bikeReservationRepository.findById(1L)).thenReturn(Optional.of(testReservation));
        when(securityService.isAdmin()).thenReturn(false);
        // Act & Assert
        assertThrows(UnauthorizedException.class, () -> bikeReservationService.getReservationById(1L));
    }

    @Test
    void createReservation_Success() {
        // Arrange
        when(securityService.hasAccessToUser(anyLong())).thenReturn(true);
        when(bikeRepository.findById(1L)).thenReturn(Optional.of(testBike));
        when(bikeReservationMapper.toEntity(testReservationDTO)).thenReturn(testReservation);
        when(bikeReservationRepository.save(any(BikeReservation.class))).thenReturn(testReservation);
        when(bikeReservationMapper.toDto(testReservation)).thenReturn(testReservationDTO);

        // Act
        BikeReservationDTO result = bikeReservationService.createReservation(testReservationDTO);

        // Assert
        assertNotNull(result);
        verify(bikeReservationRepository, times(1)).save(any(BikeReservation.class));
    }

    @Test
    void createReservation_BikeNotFound() {
        // Arrange
        when(securityService.isAdmin()).thenReturn(true);
        when(bikeRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> bikeReservationService.createReservation(testReservationDTO));
    }

    @Test
    void deleteReservation_WithAdminRole_Success() {
        // Arrange
        when(securityService.isAdmin()).thenReturn(true);
        when(bikeReservationRepository.findById(1L)).thenReturn(Optional.of(testReservation));
        doNothing().when(bikeReservationRepository).delete(testReservation);

        // Act
        bikeReservationService.deleteReservation(1L);

        // Assert
        verify(bikeReservationRepository, times(1)).findById(1L);
        verify(bikeReservationRepository, times(1)).delete(testReservation);
    }

    @Test
    void deleteReservation_WithAccessToUser_Success() {
        // Arrange
        when(bikeReservationRepository.findById(1L)).thenReturn(Optional.of(testReservation));
        when(securityService.isAdmin()).thenReturn(false);
        when(securityService.hasAccessToUser(anyLong())).thenReturn(true);
        doNothing().when(bikeReservationRepository).delete(testReservation);

        // Act
        bikeReservationService.deleteReservation(1L);

        // Assert
        verify(bikeReservationRepository, times(1)).delete(testReservation);
    }

    @Test
    void deleteReservation_WithoutAccess_ThrowsUnauthorizedException() {
        // Arrange
        when(bikeReservationRepository.findById(1L)).thenReturn(Optional.of(testReservation));
        when(securityService.isAdmin()).thenReturn(false);
        // Act & Assert
        assertThrows(UnauthorizedException.class, () -> bikeReservationService.deleteReservation(1L));
        verify(bikeReservationRepository, never()).delete(any(BikeReservation.class));
    }

    @Test
    void deleteReservation_NotFound() {
        // Arrange
        when(bikeReservationRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> bikeReservationService.deleteReservation(999L));
        verify(bikeReservationRepository, never()).delete(any(BikeReservation.class));
    }
}
