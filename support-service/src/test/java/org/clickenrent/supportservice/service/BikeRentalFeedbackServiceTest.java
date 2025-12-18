package org.clickenrent.supportservice.service;

import org.clickenrent.supportservice.dto.BikeRentalFeedbackDTO;
import org.clickenrent.supportservice.entity.BikeRentalFeedback;
import org.clickenrent.supportservice.exception.ResourceNotFoundException;
import org.clickenrent.supportservice.exception.UnauthorizedException;
import org.clickenrent.supportservice.mapper.BikeRentalFeedbackMapper;
import org.clickenrent.supportservice.repository.BikeRentalFeedbackRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BikeRentalFeedbackServiceTest {

    @Mock
    private BikeRentalFeedbackRepository bikeRentalFeedbackRepository;

    @Mock
    private BikeRentalFeedbackMapper bikeRentalFeedbackMapper;

    @Mock
    private SecurityService securityService;

    @InjectMocks
    private BikeRentalFeedbackService bikeRentalFeedbackService;

    private BikeRentalFeedback testFeedback;
    private BikeRentalFeedbackDTO testFeedbackDTO;
    private LocalDateTime testDateTime;

    @BeforeEach
    void setUp() {
        testDateTime = LocalDateTime.now();

        testFeedback = BikeRentalFeedback.builder()
                .id(1L)
                .userId(1L)
                .bikeRentalId(101L)
                .rate(5)
                .comment("Great bike!")
                .dateTime(testDateTime)
                .build();

        testFeedbackDTO = BikeRentalFeedbackDTO.builder()
                .id(1L)
                .userId(1L)
                .bikeRentalId(101L)
                .rate(5)
                .comment("Great bike!")
                .dateTime(testDateTime)
                .build();
    }

    @Test
    void getAll_AsAdmin_ReturnsAllFeedback() {
        when(securityService.isAdmin()).thenReturn(true);
        when(bikeRentalFeedbackRepository.findAll()).thenReturn(Arrays.asList(testFeedback));
        when(bikeRentalFeedbackMapper.toDto(testFeedback)).thenReturn(testFeedbackDTO);

        List<BikeRentalFeedbackDTO> result = bikeRentalFeedbackService.getAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(5, result.get(0).getRate());
        verify(bikeRentalFeedbackRepository, times(1)).findAll();
    }

    @Test
    void getAll_AsNonAdmin_ReturnsUserFeedback() {
        when(securityService.isAdmin()).thenReturn(false);
        when(securityService.getCurrentUserId()).thenReturn(1L);
        when(bikeRentalFeedbackRepository.findByUserId(1L)).thenReturn(Arrays.asList(testFeedback));
        when(bikeRentalFeedbackMapper.toDto(testFeedback)).thenReturn(testFeedbackDTO);

        List<BikeRentalFeedbackDTO> result = bikeRentalFeedbackService.getAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(bikeRentalFeedbackRepository, times(1)).findByUserId(1L);
    }

    @Test
    void getById_Success() {
        when(securityService.isAdmin()).thenReturn(true);
        when(bikeRentalFeedbackRepository.findById(1L)).thenReturn(Optional.of(testFeedback));
        when(bikeRentalFeedbackMapper.toDto(testFeedback)).thenReturn(testFeedbackDTO);

        BikeRentalFeedbackDTO result = bikeRentalFeedbackService.getById(1L);

        assertNotNull(result);
        assertEquals(5, result.getRate());
        verify(bikeRentalFeedbackRepository, times(1)).findById(1L);
    }

    @Test
    void getById_NotFound() {
        when(bikeRentalFeedbackRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bikeRentalFeedbackService.getById(999L));
    }

    @Test
    void getById_Unauthorized() {
        when(securityService.isAdmin()).thenReturn(false);
        when(securityService.hasAccessToUser(1L)).thenReturn(false);
        when(bikeRentalFeedbackRepository.findById(1L)).thenReturn(Optional.of(testFeedback));

        assertThrows(UnauthorizedException.class, () -> bikeRentalFeedbackService.getById(1L));
    }

    @Test
    void getByBikeRentalId_Success() {
        when(securityService.isAdmin()).thenReturn(true);
        when(bikeRentalFeedbackRepository.findByBikeRentalId(101L)).thenReturn(Optional.of(testFeedback));
        when(bikeRentalFeedbackMapper.toDto(testFeedback)).thenReturn(testFeedbackDTO);

        BikeRentalFeedbackDTO result = bikeRentalFeedbackService.getByBikeRentalId(101L);

        assertNotNull(result);
        assertEquals(101L, result.getBikeRentalId());
    }

    @Test
    void getByBikeRentalId_NotFound() {
        when(bikeRentalFeedbackRepository.findByBikeRentalId(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bikeRentalFeedbackService.getByBikeRentalId(999L));
    }

    @Test
    void getByUserId_Success() {
        when(securityService.isAdmin()).thenReturn(true);
        when(bikeRentalFeedbackRepository.findByUserId(1L)).thenReturn(Arrays.asList(testFeedback));
        when(bikeRentalFeedbackMapper.toDto(testFeedback)).thenReturn(testFeedbackDTO);

        List<BikeRentalFeedbackDTO> result = bikeRentalFeedbackService.getByUserId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(bikeRentalFeedbackRepository, times(1)).findByUserId(1L);
    }

    @Test
    void getByUserId_Unauthorized() {
        when(securityService.isAdmin()).thenReturn(false);
        when(securityService.hasAccessToUser(2L)).thenReturn(false);

        assertThrows(UnauthorizedException.class, () -> bikeRentalFeedbackService.getByUserId(2L));
    }

    @Test
    void create_Success() {
        when(securityService.getCurrentUserId()).thenReturn(1L);
        when(bikeRentalFeedbackMapper.toEntity(testFeedbackDTO)).thenReturn(testFeedback);
        when(bikeRentalFeedbackRepository.save(any(BikeRentalFeedback.class))).thenReturn(testFeedback);
        when(bikeRentalFeedbackMapper.toDto(testFeedback)).thenReturn(testFeedbackDTO);

        BikeRentalFeedbackDTO result = bikeRentalFeedbackService.create(testFeedbackDTO);

        assertNotNull(result);
        assertEquals(5, result.getRate());
        verify(bikeRentalFeedbackRepository, times(1)).save(any(BikeRentalFeedback.class));
    }

    @Test
    void create_WithoutUserId_SetsCurrentUser() {
        BikeRentalFeedbackDTO dtoWithoutUserId = BikeRentalFeedbackDTO.builder()
                .bikeRentalId(101L)
                .rate(5)
                .comment("Test")
                .dateTime(testDateTime)
                .build();
        
        when(securityService.getCurrentUserId()).thenReturn(1L);
        when(bikeRentalFeedbackMapper.toEntity(any(BikeRentalFeedbackDTO.class))).thenReturn(testFeedback);
        when(bikeRentalFeedbackRepository.save(any(BikeRentalFeedback.class))).thenReturn(testFeedback);
        when(bikeRentalFeedbackMapper.toDto(testFeedback)).thenReturn(testFeedbackDTO);

        BikeRentalFeedbackDTO result = bikeRentalFeedbackService.create(dtoWithoutUserId);

        assertNotNull(result);
        assertEquals(1L, dtoWithoutUserId.getUserId());
    }

    @Test
    void create_Unauthorized() {
        when(securityService.isAdmin()).thenReturn(false);
        when(securityService.getCurrentUserId()).thenReturn(1L);
        testFeedbackDTO.setUserId(2L);

        assertThrows(UnauthorizedException.class, () -> bikeRentalFeedbackService.create(testFeedbackDTO));
    }

    @Test
    void update_Success() {
        when(securityService.isAdmin()).thenReturn(true);
        when(bikeRentalFeedbackRepository.findById(1L)).thenReturn(Optional.of(testFeedback));
        doNothing().when(bikeRentalFeedbackMapper).updateEntityFromDto(testFeedbackDTO, testFeedback);
        when(bikeRentalFeedbackRepository.save(any(BikeRentalFeedback.class))).thenReturn(testFeedback);
        when(bikeRentalFeedbackMapper.toDto(testFeedback)).thenReturn(testFeedbackDTO);

        BikeRentalFeedbackDTO result = bikeRentalFeedbackService.update(1L, testFeedbackDTO);

        assertNotNull(result);
        verify(bikeRentalFeedbackRepository, times(1)).save(any(BikeRentalFeedback.class));
    }

    @Test
    void update_NotFound() {
        when(bikeRentalFeedbackRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bikeRentalFeedbackService.update(999L, testFeedbackDTO));
    }

    @Test
    void update_Unauthorized() {
        when(securityService.isAdmin()).thenReturn(false);
        when(securityService.hasAccessToUser(1L)).thenReturn(false);
        when(bikeRentalFeedbackRepository.findById(1L)).thenReturn(Optional.of(testFeedback));

        assertThrows(UnauthorizedException.class, () -> bikeRentalFeedbackService.update(1L, testFeedbackDTO));
    }

    @Test
    void delete_Success() {
        when(securityService.isAdmin()).thenReturn(true);
        when(bikeRentalFeedbackRepository.findById(1L)).thenReturn(Optional.of(testFeedback));
        doNothing().when(bikeRentalFeedbackRepository).delete(testFeedback);

        bikeRentalFeedbackService.delete(1L);

        verify(bikeRentalFeedbackRepository, times(1)).delete(testFeedback);
    }

    @Test
    void delete_NotFound() {
        when(bikeRentalFeedbackRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bikeRentalFeedbackService.delete(999L));
    }

    @Test
    void delete_Unauthorized() {
        when(securityService.isAdmin()).thenReturn(false);
        when(securityService.hasAccessToUser(1L)).thenReturn(false);
        when(bikeRentalFeedbackRepository.findById(1L)).thenReturn(Optional.of(testFeedback));

        assertThrows(UnauthorizedException.class, () -> bikeRentalFeedbackService.delete(1L));
    }
}

