package org.clickenrent.supportservice.service;

import org.clickenrent.supportservice.dto.ErrorCodeDTO;
import org.clickenrent.supportservice.entity.ErrorCode;
import org.clickenrent.supportservice.exception.ResourceNotFoundException;
import org.clickenrent.supportservice.exception.UnauthorizedException;
import org.clickenrent.supportservice.mapper.ErrorCodeMapper;
import org.clickenrent.supportservice.repository.ErrorCodeRepository;
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
class ErrorCodeServiceTest {

    @Mock
    private ErrorCodeRepository errorCodeRepository;

    @Mock
    private ErrorCodeMapper errorCodeMapper;

    @Mock
    private SecurityService securityService;

    @InjectMocks
    private ErrorCodeService errorCodeService;

    private ErrorCode testErrorCode;
    private ErrorCodeDTO testErrorCodeDTO;

    @BeforeEach
    void setUp() {
        testErrorCode = ErrorCode.builder()
                .id(1L)
                .externalId("550e8400-e29b-41d4-a716-446655440201")
                .name("E001")
                .bikeEngineExternalId("bike-engine-uuid-1")
                .description("Battery Low Voltage")
                .commonCause("Battery discharged or faulty cell")
                .diagnosticSteps("Check battery voltage with multimeter")
                .recommendedFix("Charge or replace battery")
                .notes("Common error in cold weather")
                .isFixableByClient(false)
                .build();

        testErrorCodeDTO = ErrorCodeDTO.builder()
                .id(1L)
                .externalId("550e8400-e29b-41d4-a716-446655440201")
                .name("E001")
                .bikeEngineExternalId("bike-engine-uuid-1")
                .description("Battery Low Voltage")
                .commonCause("Battery discharged or faulty cell")
                .isFixableByClient(false)
                .build();
    }

    @Test
    void getAll_ReturnsAllErrorCodes() {
        when(errorCodeRepository.findAll()).thenReturn(Arrays.asList(testErrorCode));
        when(errorCodeMapper.toDto(testErrorCode)).thenReturn(testErrorCodeDTO);

        List<ErrorCodeDTO> result = errorCodeService.getAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("E001", result.get(0).getName());
        verify(errorCodeRepository, times(1)).findAll();
    }

    @Test
    void getById_Success() {
        when(errorCodeRepository.findById(1L)).thenReturn(Optional.of(testErrorCode));
        when(errorCodeMapper.toDto(testErrorCode)).thenReturn(testErrorCodeDTO);

        ErrorCodeDTO result = errorCodeService.getById(1L);

        assertNotNull(result);
        assertEquals("E001", result.getName());
        verify(errorCodeRepository, times(1)).findById(1L);
    }

    @Test
    void getById_NotFound() {
        when(errorCodeRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> errorCodeService.getById(999L));
    }

    @Test
    void getByExternalId_Success() {
        when(errorCodeRepository.findByExternalId("550e8400-e29b-41d4-a716-446655440201"))
                .thenReturn(Optional.of(testErrorCode));
        when(errorCodeMapper.toDto(testErrorCode)).thenReturn(testErrorCodeDTO);

        ErrorCodeDTO result = errorCodeService.getByExternalId("550e8400-e29b-41d4-a716-446655440201");

        assertNotNull(result);
        assertEquals("E001", result.getName());
    }

    @Test
    void getByExternalId_NotFound() {
        when(errorCodeRepository.findByExternalId("invalid")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> errorCodeService.getByExternalId("invalid"));
    }

    @Test
    void getByBikeEngineExternalId_Success() {
        when(errorCodeRepository.findByBikeEngineExternalId("bike-engine-uuid-1")).thenReturn(Arrays.asList(testErrorCode));
        when(errorCodeMapper.toDto(testErrorCode)).thenReturn(testErrorCodeDTO);

        List<ErrorCodeDTO> result = errorCodeService.getByBikeEngineExternalId("bike-engine-uuid-1");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("E001", result.get(0).getName());
        verify(errorCodeRepository, times(1)).findByBikeEngineExternalId("bike-engine-uuid-1");
    }

    @Test
    void create_Success() {
        when(securityService.isAdmin()).thenReturn(true);
        when(errorCodeMapper.toEntity(testErrorCodeDTO)).thenReturn(testErrorCode);
        when(errorCodeRepository.save(any(ErrorCode.class))).thenReturn(testErrorCode);
        when(errorCodeMapper.toDto(testErrorCode)).thenReturn(testErrorCodeDTO);

        ErrorCodeDTO result = errorCodeService.create(testErrorCodeDTO);

        assertNotNull(result);
        assertEquals("E001", result.getName());
        verify(errorCodeRepository, times(1)).save(any(ErrorCode.class));
    }

    @Test
    void create_Unauthorized() {
        when(securityService.isAdmin()).thenReturn(false);

        assertThrows(UnauthorizedException.class, () -> errorCodeService.create(testErrorCodeDTO));
        verify(errorCodeRepository, never()).save(any(ErrorCode.class));
    }

    @Test
    void update_Success() {
        when(securityService.isAdmin()).thenReturn(true);
        when(errorCodeRepository.findById(1L)).thenReturn(Optional.of(testErrorCode));
        doNothing().when(errorCodeMapper).updateEntityFromDto(testErrorCodeDTO, testErrorCode);
        when(errorCodeRepository.save(any(ErrorCode.class))).thenReturn(testErrorCode);
        when(errorCodeMapper.toDto(testErrorCode)).thenReturn(testErrorCodeDTO);

        ErrorCodeDTO result = errorCodeService.update(1L, testErrorCodeDTO);

        assertNotNull(result);
        verify(errorCodeRepository, times(1)).save(any(ErrorCode.class));
    }

    @Test
    void update_NotFound() {
        when(securityService.isAdmin()).thenReturn(true);
        when(errorCodeRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> errorCodeService.update(999L, testErrorCodeDTO));
    }

    @Test
    void update_Unauthorized() {
        when(securityService.isAdmin()).thenReturn(false);

        assertThrows(UnauthorizedException.class, () -> errorCodeService.update(1L, testErrorCodeDTO));
    }

    @Test
    void delete_Success() {
        when(securityService.isAdmin()).thenReturn(true);
        when(errorCodeRepository.findById(1L)).thenReturn(Optional.of(testErrorCode));
        doNothing().when(errorCodeRepository).delete(testErrorCode);

        errorCodeService.delete(1L);

        verify(errorCodeRepository, times(1)).delete(testErrorCode);
    }

    @Test
    void delete_NotFound() {
        when(securityService.isAdmin()).thenReturn(true);
        when(errorCodeRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> errorCodeService.delete(999L));
    }

    @Test
    void delete_Unauthorized() {
        when(securityService.isAdmin()).thenReturn(false);

        assertThrows(UnauthorizedException.class, () -> errorCodeService.delete(1L));
    }
}


