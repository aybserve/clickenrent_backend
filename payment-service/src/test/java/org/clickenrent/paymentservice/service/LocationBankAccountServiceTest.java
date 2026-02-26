package org.clickenrent.paymentservice.service;

import org.clickenrent.paymentservice.dto.LocationBankAccountDTO;
import org.clickenrent.paymentservice.entity.LocationBankAccount;
import org.clickenrent.paymentservice.mapper.LocationBankAccountMapper;
import org.clickenrent.paymentservice.repository.LocationBankAccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LocationBankAccountServiceTest {

    @Mock
    private LocationBankAccountRepository locationBankAccountRepository;

    @Mock
    private LocationBankAccountMapper locationBankAccountMapper;

    @InjectMocks
    private LocationBankAccountService locationBankAccountService;

    private LocationBankAccountDTO dto;
    private LocationBankAccount entity;
    private LocationBankAccount savedEntity;

    @BeforeEach
    void setUp() {
        dto = LocationBankAccountDTO.builder()
                .externalId("ext-1")
                .locationExternalId("location-1")
                .accountHolderName("Holder")
                .iban("NL91ABNA0417164300")
                .currency("EUR")
                .build();
        entity = new LocationBankAccount();
        entity.setExternalId("ext-1");
        entity.setLocationExternalId("location-1");
        savedEntity = new LocationBankAccount();
        savedEntity.setExternalId("ext-1");
        savedEntity.setLocationExternalId("location-1");
    }

    @Test
    void createLocationBankAccount_WhenNoExistingAccount_ReturnsDto() {
        when(locationBankAccountRepository.existsByLocationExternalId("location-1")).thenReturn(false);
        when(locationBankAccountMapper.toEntity(dto)).thenReturn(entity);
        when(locationBankAccountRepository.save(any(LocationBankAccount.class))).thenReturn(savedEntity);
        when(locationBankAccountMapper.toDTO(savedEntity)).thenReturn(dto);

        LocationBankAccountDTO result = locationBankAccountService.createLocationBankAccount(dto);

        assertNotNull(result);
        assertEquals("location-1", result.getLocationExternalId());
        verify(locationBankAccountRepository).save(any(LocationBankAccount.class));
    }

    @Test
    void createLocationBankAccount_WhenAccountExists_ThrowsIllegalStateException() {
        when(locationBankAccountRepository.existsByLocationExternalId("location-1")).thenReturn(true);

        assertThrows(IllegalStateException.class, () ->
                locationBankAccountService.createLocationBankAccount(dto));
        verify(locationBankAccountRepository, never()).save(any());
    }

    @Test
    void getByLocationExternalId_WhenFound_ReturnsDto() {
        when(locationBankAccountRepository.findByLocationExternalId("location-1")).thenReturn(Optional.of(entity));
        when(locationBankAccountMapper.toDTO(entity)).thenReturn(dto);

        LocationBankAccountDTO result = locationBankAccountService.getByLocationExternalId("location-1");

        assertNotNull(result);
        assertEquals("location-1", result.getLocationExternalId());
    }

    @Test
    void getByLocationExternalId_WhenNotFound_ReturnsNull() {
        when(locationBankAccountRepository.findByLocationExternalId("missing")).thenReturn(Optional.empty());

        LocationBankAccountDTO result = locationBankAccountService.getByLocationExternalId("missing");

        assertNull(result);
    }

    @Test
    void getByExternalId_WhenFound_ReturnsDto() {
        when(locationBankAccountRepository.findByExternalId("ext-1")).thenReturn(Optional.of(entity));
        when(locationBankAccountMapper.toDTO(entity)).thenReturn(dto);

        LocationBankAccountDTO result = locationBankAccountService.getByExternalId("ext-1");

        assertNotNull(result);
        assertEquals("ext-1", result.getExternalId());
    }

    @Test
    void getByExternalId_WhenNotFound_ReturnsNull() {
        when(locationBankAccountRepository.findByExternalId("missing")).thenReturn(Optional.empty());

        LocationBankAccountDTO result = locationBankAccountService.getByExternalId("missing");

        assertNull(result);
    }
}
