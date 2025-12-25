package org.clickenrent.rentalservice.service;

import org.clickenrent.rentalservice.dto.KeyDTO;
import org.clickenrent.rentalservice.entity.Key;
import org.clickenrent.rentalservice.entity.Lock;
import org.clickenrent.rentalservice.exception.ResourceNotFoundException;
import org.clickenrent.rentalservice.mapper.KeyMapper;
import org.clickenrent.rentalservice.repository.KeyRepository;
import org.clickenrent.rentalservice.repository.LockRepository;
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
class KeyServiceTest {

    @Mock
    private KeyRepository keyRepository;

    @Mock
    private LockRepository lockRepository;

    @Mock
    private KeyMapper keyMapper;

    @Mock
    private SecurityService securityService;

    @InjectMocks
    private KeyService keyService;

    private Key testKey;
    private KeyDTO testKeyDTO;
    private Lock testLock;

    @BeforeEach
    void setUp() {
        testLock = Lock.builder().id(1L).externalId("LOCK001").build();
        
        testKey = Key.builder()
        .id(1L)
        .externalId("KEY001")
        .lock(testLock)
        .build();

        testKeyDTO = KeyDTO.builder()
        .id(1L)
        .externalId("KEY001")
        .lockId(1L)
        .build();
        
    }

    @Test
    void getKeysByLock_ReturnsAll() {
        when(securityService.isAdmin()).thenReturn(true);
        when(lockRepository.findById(1L)).thenReturn(Optional.of(testLock));
        when(keyRepository.findByLock(testLock)).thenReturn(Collections.singletonList(testKey));
        when(keyMapper.toDto(testKey)).thenReturn(testKeyDTO);

        List<KeyDTO> result = keyService.getKeysByLock(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("KEY001", result.get(0).getExternalId());
        verify(keyRepository, times(1)).findByLock(testLock);
    }

    @Test
    void getKeyById_Success() {
        when(securityService.isAdmin()).thenReturn(true);
        when(keyRepository.findById(1L)).thenReturn(Optional.of(testKey));
        when(keyMapper.toDto(testKey)).thenReturn(testKeyDTO);

        KeyDTO result = keyService.getKeyById(1L);

        assertNotNull(result);
        assertEquals("KEY001", result.getExternalId());
        verify(keyRepository, times(1)).findById(1L);
    }

    @Test
    void getKeyById_NotFound() {
        when(securityService.isAdmin()).thenReturn(true);
        when(keyRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> keyService.getKeyById(999L));
    }

    @Test
    void createKey_Success() {
        when(securityService.isAdmin()).thenReturn(true);
        when(keyMapper.toEntity(testKeyDTO)).thenReturn(testKey);
        when(keyRepository.save(any())).thenReturn(testKey);
        when(keyMapper.toDto(testKey)).thenReturn(testKeyDTO);

        KeyDTO result = keyService.createKey(testKeyDTO);

        assertNotNull(result);
        assertEquals("KEY001", result.getExternalId());
        verify(keyRepository, times(1)).save(any());
    }

    @Test
    void deleteKey_Success() {
        when(securityService.isAdmin()).thenReturn(true);
        when(keyRepository.findById(1L)).thenReturn(Optional.of(testKey));
        doNothing().when(keyRepository).delete(testKey);

        keyService.deleteKey(1L);

        verify(keyRepository, times(1)).delete(testKey);
    }

    @Test
    void deleteKey_NotFound() {
        when(securityService.isAdmin()).thenReturn(true);
        when(keyRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> keyService.deleteKey(999L));
    }
}






