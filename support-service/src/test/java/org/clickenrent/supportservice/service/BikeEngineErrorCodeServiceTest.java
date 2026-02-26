package org.clickenrent.supportservice.service;

import org.clickenrent.supportservice.dto.BikeEngineErrorCodeDTO;
import org.clickenrent.supportservice.entity.BikeEngineErrorCode;
import org.clickenrent.supportservice.entity.ErrorCode;
import org.clickenrent.supportservice.exception.ResourceNotFoundException;
import org.clickenrent.supportservice.exception.UnauthorizedException;
import org.clickenrent.supportservice.mapper.BikeEngineErrorCodeMapper;
import org.clickenrent.supportservice.repository.BikeEngineErrorCodeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BikeEngineErrorCodeServiceTest {

    @Mock
    private BikeEngineErrorCodeRepository bikeEngineErrorCodeRepository;

    @Mock
    private BikeEngineErrorCodeMapper bikeEngineErrorCodeMapper;

    @Mock
    private SecurityService securityService;

    @InjectMocks
    private BikeEngineErrorCodeService bikeEngineErrorCodeService;

    private BikeEngineErrorCode testEntity;
    private BikeEngineErrorCodeDTO testDTO;

    @BeforeEach
    void setUp() {
        ErrorCode errorCode = ErrorCode.builder().id(1L).name("E001").build();
        testEntity = BikeEngineErrorCode.builder()
                .id(1L)
                .externalId("link-uuid-1")
                .bikeEngineExternalId("engine-uuid-1")
                .errorCode(errorCode)
                .build();

        testDTO = BikeEngineErrorCodeDTO.builder()
                .id(1L)
                .externalId("link-uuid-1")
                .bikeEngineExternalId("engine-uuid-1")
                .errorCodeId(1L)
                .errorCodeName("E001")
                .build();
    }

    @Test
    void getAll_ReturnsAll() {
        when(bikeEngineErrorCodeRepository.findAll()).thenReturn(Arrays.asList(testEntity));
        when(bikeEngineErrorCodeMapper.toDto(testEntity)).thenReturn(testDTO);

        List<BikeEngineErrorCodeDTO> result = bikeEngineErrorCodeService.getAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("engine-uuid-1", result.get(0).getBikeEngineExternalId());
        verify(bikeEngineErrorCodeRepository, times(1)).findAll();
    }

    @Test
    void getById_Success() {
        when(bikeEngineErrorCodeRepository.findById(1L)).thenReturn(Optional.of(testEntity));
        when(bikeEngineErrorCodeMapper.toDto(testEntity)).thenReturn(testDTO);

        BikeEngineErrorCodeDTO result = bikeEngineErrorCodeService.getById(1L);

        assertNotNull(result);
        assertEquals("link-uuid-1", result.getExternalId());
        verify(bikeEngineErrorCodeRepository, times(1)).findById(1L);
    }

    @Test
    void getById_NotFound() {
        when(bikeEngineErrorCodeRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bikeEngineErrorCodeService.getById(999L));
    }

    @Test
    void getByExternalId_Success() {
        when(bikeEngineErrorCodeRepository.findByExternalId("link-uuid-1")).thenReturn(Optional.of(testEntity));
        when(bikeEngineErrorCodeMapper.toDto(testEntity)).thenReturn(testDTO);

        BikeEngineErrorCodeDTO result = bikeEngineErrorCodeService.getByExternalId("link-uuid-1");

        assertNotNull(result);
        assertEquals("link-uuid-1", result.getExternalId());
    }

    @Test
    void getByExternalId_NotFound() {
        when(bikeEngineErrorCodeRepository.findByExternalId("invalid")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bikeEngineErrorCodeService.getByExternalId("invalid"));
    }

    @Test
    void getByBikeEngineExternalId_Success() {
        when(bikeEngineErrorCodeRepository.findByBikeEngineExternalId("engine-uuid-1")).thenReturn(Arrays.asList(testEntity));
        when(bikeEngineErrorCodeMapper.toDto(testEntity)).thenReturn(testDTO);

        List<BikeEngineErrorCodeDTO> result = bikeEngineErrorCodeService.getByBikeEngineExternalId("engine-uuid-1");

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(bikeEngineErrorCodeRepository, times(1)).findByBikeEngineExternalId("engine-uuid-1");
    }

    @Test
    void getByErrorCodeId_Success() {
        when(bikeEngineErrorCodeRepository.findByErrorCodeId(1L)).thenReturn(Arrays.asList(testEntity));
        when(bikeEngineErrorCodeMapper.toDto(testEntity)).thenReturn(testDTO);

        List<BikeEngineErrorCodeDTO> result = bikeEngineErrorCodeService.getByErrorCodeId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(bikeEngineErrorCodeRepository, times(1)).findByErrorCodeId(1L);
    }

    @Test
    void create_Success() {
        when(securityService.isAdmin()).thenReturn(true);
        when(bikeEngineErrorCodeMapper.toEntity(testDTO)).thenReturn(testEntity);
        when(bikeEngineErrorCodeRepository.save(any(BikeEngineErrorCode.class))).thenReturn(testEntity);
        when(bikeEngineErrorCodeMapper.toDto(testEntity)).thenReturn(testDTO);

        BikeEngineErrorCodeDTO result = bikeEngineErrorCodeService.create(testDTO);

        assertNotNull(result);
        verify(bikeEngineErrorCodeRepository, times(1)).save(any(BikeEngineErrorCode.class));
    }

    @Test
    void create_Unauthorized() {
        when(securityService.isAdmin()).thenReturn(false);

        assertThrows(UnauthorizedException.class, () -> bikeEngineErrorCodeService.create(testDTO));
        verify(bikeEngineErrorCodeRepository, never()).save(any(BikeEngineErrorCode.class));
    }

    @Test
    void update_Success() {
        when(securityService.isAdmin()).thenReturn(true);
        when(bikeEngineErrorCodeRepository.findById(1L)).thenReturn(Optional.of(testEntity));
        doNothing().when(bikeEngineErrorCodeMapper).updateEntityFromDto(testDTO, testEntity);
        when(bikeEngineErrorCodeRepository.save(any(BikeEngineErrorCode.class))).thenReturn(testEntity);
        when(bikeEngineErrorCodeMapper.toDto(testEntity)).thenReturn(testDTO);

        BikeEngineErrorCodeDTO result = bikeEngineErrorCodeService.update(1L, testDTO);

        assertNotNull(result);
        verify(bikeEngineErrorCodeRepository, times(1)).save(any(BikeEngineErrorCode.class));
    }

    @Test
    void update_NotFound() {
        when(securityService.isAdmin()).thenReturn(true);
        when(bikeEngineErrorCodeRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bikeEngineErrorCodeService.update(999L, testDTO));
    }

    @Test
    void update_Unauthorized() {
        when(securityService.isAdmin()).thenReturn(false);

        assertThrows(UnauthorizedException.class, () -> bikeEngineErrorCodeService.update(1L, testDTO));
    }

    @Test
    void delete_Success() {
        when(securityService.isAdmin()).thenReturn(true);
        when(bikeEngineErrorCodeRepository.findById(1L)).thenReturn(Optional.of(testEntity));
        doNothing().when(bikeEngineErrorCodeRepository).delete(testEntity);

        bikeEngineErrorCodeService.delete(1L);

        verify(bikeEngineErrorCodeRepository, times(1)).delete(testEntity);
    }

    @Test
    void delete_NotFound() {
        when(securityService.isAdmin()).thenReturn(true);
        when(bikeEngineErrorCodeRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bikeEngineErrorCodeService.delete(999L));
    }

    @Test
    void delete_Unauthorized() {
        when(securityService.isAdmin()).thenReturn(false);

        assertThrows(UnauthorizedException.class, () -> bikeEngineErrorCodeService.delete(1L));
    }
}
