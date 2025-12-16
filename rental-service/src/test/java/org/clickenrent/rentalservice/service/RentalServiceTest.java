package org.clickenrent.rentalservice.service;

import org.clickenrent.rentalservice.dto.RentalDTO;
import org.clickenrent.rentalservice.entity.Rental;
import org.clickenrent.rentalservice.exception.ResourceNotFoundException;
import org.clickenrent.rentalservice.exception.UnauthorizedException;
import org.clickenrent.rentalservice.mapper.RentalMapper;
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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for RentalService.
 */
@ExtendWith(MockitoExtension.class)
class RentalServiceTest {

    @Mock
    private RentalRepository rentalRepository;

    @Mock
    private RentalMapper rentalMapper;

    @Mock
    private SecurityService securityService;


    @InjectMocks
    private RentalService rentalService;

    private Rental testRental;
    private RentalDTO testRentalDTO;

    @BeforeEach
    void setUp() {
        testRental = Rental.builder()
        .id(1L)
        .externalId("RENT001")
        .userId(1L)
        .companyId(1L)
        .build();

        testRentalDTO = RentalDTO.builder()
        .id(1L)
        .externalId("RENT001")
        .userId(1L)
        .companyId(1L)
        .rentalStatusId(2L)
        .build();
    }

    @Test
    void getAllRentals_WithAdminRole_ReturnsAllRentals() {
        // Arrange
        when(securityService.isAdmin()).thenReturn(true);
        Pageable pageable = PageRequest.of(0, 20);
        Page<Rental> rentalPage = new PageImpl<>(Collections.singletonList(testRental));
        when(rentalRepository.findAll(pageable)).thenReturn(rentalPage);
        when(rentalMapper.toDto(testRental)).thenReturn(testRentalDTO);

        // Act
        Page<RentalDTO> result = rentalService.getAllRentals(pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("RENT001", result.getContent().get(0).getExternalId());
        verify(rentalRepository, times(1)).findAll(pageable);
    }

    @Test
    void getAllRentals_WithB2BRole_ReturnsCompanyRentals() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 20);
        when(securityService.isAdmin()).thenReturn(false);
        when(securityService.isB2B()).thenReturn(true);
        when(securityService.getCurrentUserCompanyIds()).thenReturn(Arrays.asList(1L));
        when(rentalRepository.findAll()).thenReturn(Collections.singletonList(testRental));
        when(rentalMapper.toDto(testRental)).thenReturn(testRentalDTO);

        // Act
        Page<RentalDTO> result = rentalService.getAllRentals(pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void getAllRentals_WithCustomerRole_ReturnsOwnRentals() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 20);
        when(securityService.isAdmin()).thenReturn(false);
        when(securityService.isB2B()).thenReturn(false);
        when(securityService.isCustomer()).thenReturn(true);
        when(securityService.getCurrentUserId()).thenReturn(1L);
        when(rentalRepository.findByUserId(1L)).thenReturn(Collections.singletonList(testRental));
        when(rentalMapper.toDto(testRental)).thenReturn(testRentalDTO);

        // Act
        Page<RentalDTO> result = rentalService.getAllRentals(pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void getAllRentals_WithoutPermissions_ThrowsUnauthorizedException() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 20);
        when(securityService.isAdmin()).thenReturn(false);
        when(securityService.isB2B()).thenReturn(false);
        // Act & Assert
        assertThrows(UnauthorizedException.class, () -> rentalService.getAllRentals(pageable));
    }

    @Test
    void getRentalById_WithAdminRole_Success() {
        // Arrange
        when(securityService.isAdmin()).thenReturn(true);
        when(rentalRepository.findById(1L)).thenReturn(Optional.of(testRental));
        when(rentalMapper.toDto(testRental)).thenReturn(testRentalDTO);

        // Act
        RentalDTO result = rentalService.getRentalById(1L);

        // Assert
        assertNotNull(result);
        assertEquals("RENT001", result.getExternalId());
        verify(rentalRepository, times(1)).findById(1L);
    }

    @Test
    void getRentalById_NotFound() {
        // Arrange
        when(rentalRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> rentalService.getRentalById(999L));
    }

    @Test
    void getRentalById_WithoutAccess_ThrowsUnauthorizedException() {
        // Arrange
        when(rentalRepository.findById(1L)).thenReturn(Optional.of(testRental));
        when(securityService.isAdmin()).thenReturn(false);
        when(securityService.hasAccessToUser(1L)).thenReturn(false);
        // Act & Assert
        assertThrows(UnauthorizedException.class, () -> rentalService.getRentalById(1L));
    }

    @Test
    void createRental_Success() {
        when(securityService.hasAccessToUser(anyLong())).thenReturn(true);
        // Arrange
        when(rentalMapper.toEntity(testRentalDTO)).thenReturn(testRental);
        when(rentalRepository.save(any(Rental.class))).thenReturn(testRental);
        when(rentalMapper.toDto(testRental)).thenReturn(testRentalDTO);

        // Act
        RentalDTO result = rentalService.createRental(testRentalDTO);

        // Assert
        assertNotNull(result);
        assertEquals("RENT001", result.getExternalId());
        verify(rentalRepository, times(1)).save(any(Rental.class));
    }

    @Test
    void updateRental_WithAdminRole_Success() {
        when(securityService.isAdmin()).thenReturn(true);
        // Arrange
        when(rentalRepository.findById(1L)).thenReturn(Optional.of(testRental));
        when(rentalRepository.save(any(Rental.class))).thenReturn(testRental);
        when(rentalMapper.toDto(testRental)).thenReturn(testRentalDTO);

        // Act
        RentalDTO result = rentalService.updateRental(1L, testRentalDTO);

        // Assert
        assertNotNull(result);
        verify(rentalRepository, times(1)).findById(1L);
        verify(rentalRepository, times(1)).save(any(Rental.class));
    }

    @Test
    void updateRental_NotFound() {
        // Arrange
        when(rentalRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> rentalService.updateRental(999L, testRentalDTO));
    }

    @Test
    void updateRental_WithoutAccess_ThrowsUnauthorizedException() {
        // Arrange
        when(rentalRepository.findById(1L)).thenReturn(Optional.of(testRental));
        when(securityService.isAdmin()).thenReturn(false);
        when(securityService.hasAccessToUser(1L)).thenReturn(false);
        // Act & Assert
        assertThrows(UnauthorizedException.class, () -> rentalService.updateRental(1L, testRentalDTO));
    }

    @Test
    void deleteRental_WithAdminRole_Success() {
        when(securityService.isAdmin()).thenReturn(true);
        // Arrange
        when(rentalRepository.findById(1L)).thenReturn(Optional.of(testRental));
        doNothing().when(rentalRepository).delete(testRental);

        // Act
        rentalService.deleteRental(1L);

        // Assert
        verify(rentalRepository, times(1)).findById(1L);
        verify(rentalRepository, times(1)).delete(testRental);
    }

    @Test
    void deleteRental_WithoutAdminRole_ThrowsUnauthorizedException() {
        // Arrange
        when(rentalRepository.findById(1L)).thenReturn(Optional.of(testRental));
        // Act & Assert
        assertThrows(UnauthorizedException.class, () -> rentalService.deleteRental(1L));
        verify(rentalRepository, never()).delete(any(Rental.class));
    }

    @Test
    void deleteRental_NotFound() {
        // Arrange
        when(rentalRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> rentalService.deleteRental(999L));
        verify(rentalRepository, never()).delete(any(Rental.class));
    }
}
