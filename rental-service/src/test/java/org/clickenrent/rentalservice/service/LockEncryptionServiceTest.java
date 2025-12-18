package org.clickenrent.rentalservice.service;

import org.clickenrent.rentalservice.entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class LockEncryptionServiceTest {

    @InjectMocks
    private LockEncryptionService lockEncryptionService;

    private BikeRental bikeRental;
    private Lock lock;
    private LockProvider lockProvider;

    @BeforeEach
    void setUp() {
        // Set configuration values
        ReflectionTestUtils.setField(lockEncryptionService, "defaultEncryptionKey", "test-encryption-key-32-chars!!");
        ReflectionTestUtils.setField(lockEncryptionService, "tokenExpirationSeconds", 300);

        // Create test data
        lockProvider = LockProvider.builder()
                .id(1L)
                .name("AXA")
                .encryptionKey("provider-specific-key-32-char!")
                .isActive(true)
                .build();

        lock = Lock.builder()
                .id(1L)
                .externalId("axa-lock-123")
                .macAddress("AA:BB:CC:DD:EE:FF")
                .lockProvider(lockProvider)
                .build();

        Bike bike = Bike.builder()
                .id(1L)
                .code("BIKE001")
                .lock(lock)
                .build();

        Rental rental = Rental.builder()
                .id(1L)
                .userId(100L)
                .companyId(1L)
                .build();

        bikeRental = BikeRental.builder()
                .id(1L)
                .bike(bike)
                .rental(rental)
                .startDateTime(LocalDateTime.now())
                .build();
    }

    @Test
    void testGenerateUnlockToken_Success() {
        // When
        String token = lockEncryptionService.generateUnlockToken(bikeRental, lock);

        // Then
        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(token.length() > 0);
    }

    @Test
    void testGenerateUnlockToken_WithDefaultKey() {
        // Given - lock without provider-specific key
        lock.setLockProvider(null);

        // When
        String token = lockEncryptionService.generateUnlockToken(bikeRental, lock);

        // Then
        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void testValidateUnlockToken_ValidToken() {
        // Given
        String token = lockEncryptionService.generateUnlockToken(bikeRental, lock);

        // When
        boolean isValid = lockEncryptionService.validateUnlockToken(token, lock);

        // Then
        assertTrue(isValid);
    }

    @Test
    void testValidateUnlockToken_InvalidToken() {
        // Given
        String invalidToken = "invalid-token";

        // When
        boolean isValid = lockEncryptionService.validateUnlockToken(invalidToken, lock);

        // Then
        assertFalse(isValid);
    }

    @Test
    void testGetTokenExpirationSeconds() {
        // When
        int expirationSeconds = lockEncryptionService.getTokenExpirationSeconds();

        // Then
        assertEquals(300, expirationSeconds);
    }
}

