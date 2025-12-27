package org.clickenrent.rentalservice.service;

import org.clickenrent.rentalservice.dto.LockDTO;
import org.clickenrent.rentalservice.entity.Lock;
import org.clickenrent.rentalservice.exception.ResourceNotFoundException;
import org.clickenrent.rentalservice.mapper.LockMapper;
import org.clickenrent.rentalservice.repository.LockRepository;
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

@ExtendWith(MockitoExtension.class)
class LockServiceTest {

    @Mock
    private LockRepository lockRepository;

    @Mock
    private LockMapper lockMapper;

    @Mock
    private SecurityService securityService;

    @InjectMocks
    private LockService lockService;

    private Lock testLock;
    private LockDTO testLockDTO;

    @BeforeEach
    void setUp() {
        testLock = Lock.builder()
        .id(1L)
        .externalId("LOCK001")
        .macAddress("AA:BB:CC:DD:EE:FF")
        .build();

        testLockDTO = LockDTO.builder()
        .id(1L)
        .externalId("LOCK001")
        .macAddress("AA:BB:CC:DD:EE:FF")
        .build();
        
    }

    @Test
    void getAllLocks_ReturnsAllLocks() {
        when(securityService.isAdmin()).thenReturn(true);
        Pageable pageable = PageRequest.of(0, 20);
        Page<Lock> lockPage = new PageImpl<>(Collections.singletonList(testLock));
        when(lockRepository.findAll(pageable)).thenReturn(lockPage);
        when(lockMapper.toDto(testLock)).thenReturn(testLockDTO);

        Page<LockDTO> result = lockService.getAllLocks(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(lockRepository, times(1)).findAll(pageable);
    }

    @Test
    void getLockById_Success() {
        when(securityService.isAdmin()).thenReturn(true);
        when(lockRepository.findById(1L)).thenReturn(Optional.of(testLock));
        when(lockMapper.toDto(testLock)).thenReturn(testLockDTO);

        LockDTO result = lockService.getLockById(1L);

        assertNotNull(result);
        assertEquals("LOCK001", result.getExternalId());
        verify(lockRepository, times(1)).findById(1L);
    }

    @Test
    void getLockById_NotFound() {
        when(securityService.isAdmin()).thenReturn(true);
        when(lockRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> lockService.getLockById(999L));
    }

    @Test
    void createLock_Success() {
        when(securityService.isAdmin()).thenReturn(true);
        when(lockMapper.toEntity(testLockDTO)).thenReturn(testLock);
        when(lockRepository.save(any())).thenReturn(testLock);
        when(lockMapper.toDto(testLock)).thenReturn(testLockDTO);

        LockDTO result = lockService.createLock(testLockDTO);

        assertNotNull(result);
        verify(lockRepository, times(1)).save(any());
    }

    @Test
    void updateLock_Success() {
        when(securityService.isAdmin()).thenReturn(true);
        when(lockRepository.findById(1L)).thenReturn(Optional.of(testLock));
        when(lockRepository.save(any())).thenReturn(testLock);
        when(lockMapper.toDto(testLock)).thenReturn(testLockDTO);

        LockDTO result = lockService.updateLock(1L, testLockDTO);

        assertNotNull(result);
        verify(lockRepository, times(1)).save(any());
    }

    @Test
    void deleteLock_Success() {
        when(securityService.isAdmin()).thenReturn(true);
        when(lockRepository.findById(1L)).thenReturn(Optional.of(testLock));
        doNothing().when(lockRepository).delete(testLock);

        lockService.deleteLock(1L);

        verify(lockRepository, times(1)).delete(testLock);
    }
}







