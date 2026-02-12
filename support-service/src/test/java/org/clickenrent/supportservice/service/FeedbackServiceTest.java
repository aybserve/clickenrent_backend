package org.clickenrent.supportservice.service;

import org.clickenrent.supportservice.dto.FeedbackDTO;
import org.clickenrent.supportservice.entity.Feedback;
import org.clickenrent.supportservice.exception.ResourceNotFoundException;
import org.clickenrent.supportservice.exception.UnauthorizedException;
import org.clickenrent.supportservice.mapper.FeedbackMapper;
import org.clickenrent.supportservice.repository.FeedbackRepository;
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
class FeedbackServiceTest {

    @Mock
    private FeedbackRepository feedbackRepository;

    @Mock
    private FeedbackMapper feedbackMapper;

    @Mock
    private SecurityService securityService;

    @InjectMocks
    private FeedbackService feedbackService;

    private Feedback testFeedback;
    private FeedbackDTO testFeedbackDTO;

    @BeforeEach
    void setUp() {
        testFeedback = Feedback.builder()
                .id(1L)
                .externalId("550e8400-e29b-41d4-a716-446655440301")
                .userExternalId("user-uuid-1")
                .rate(5)
                .comment("Excellent service")
                .build();

        testFeedbackDTO = FeedbackDTO.builder()
                .id(1L)
                .externalId("550e8400-e29b-41d4-a716-446655440301")
                .userExternalId("user-uuid-1")
                .rate(5)
                .comment("Excellent service")
                .build();
    }

    @Test
    void getAll_AsAdmin_ReturnsAllFeedback() {
        when(securityService.isAdmin()).thenReturn(true);
        when(feedbackRepository.findAll()).thenReturn(Arrays.asList(testFeedback));
        when(feedbackMapper.toDto(testFeedback)).thenReturn(testFeedbackDTO);

        List<FeedbackDTO> result = feedbackService.getAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(5, result.get(0).getRate());
        verify(feedbackRepository, times(1)).findAll();
    }

    @Test
    void getAll_AsNonAdmin_ReturnsUserFeedback() {
        when(securityService.isAdmin()).thenReturn(false);
        when(securityService.getCurrentUserExternalId()).thenReturn("user-uuid-1");
        when(feedbackRepository.findByUserExternalId("user-uuid-1")).thenReturn(Arrays.asList(testFeedback));
        when(feedbackMapper.toDto(testFeedback)).thenReturn(testFeedbackDTO);

        List<FeedbackDTO> result = feedbackService.getAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(feedbackRepository, times(1)).findByUserExternalId("user-uuid-1");
    }

    @Test
    void getById_Success() {
        when(securityService.isAdmin()).thenReturn(true);
        when(feedbackRepository.findById(1L)).thenReturn(Optional.of(testFeedback));
        when(feedbackMapper.toDto(testFeedback)).thenReturn(testFeedbackDTO);

        FeedbackDTO result = feedbackService.getById(1L);

        assertNotNull(result);
        assertEquals(5, result.getRate());
        verify(feedbackRepository, times(1)).findById(1L);
    }

    @Test
    void getById_NotFound() {
        when(feedbackRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> feedbackService.getById(999L));
    }

    @Test
    void getById_Unauthorized() {
        when(securityService.isAdmin()).thenReturn(false);
        when(securityService.getCurrentUserExternalId()).thenReturn("user-uuid-2");
        when(feedbackRepository.findById(1L)).thenReturn(Optional.of(testFeedback));

        assertThrows(UnauthorizedException.class, () -> feedbackService.getById(1L));
    }

    @Test
    void getByExternalId_Success() {
        when(securityService.isAdmin()).thenReturn(true);
        when(feedbackRepository.findByExternalId("550e8400-e29b-41d4-a716-446655440301"))
                .thenReturn(Optional.of(testFeedback));
        when(feedbackMapper.toDto(testFeedback)).thenReturn(testFeedbackDTO);

        FeedbackDTO result = feedbackService.getByExternalId("550e8400-e29b-41d4-a716-446655440301");

        assertNotNull(result);
        assertEquals(5, result.getRate());
    }

    @Test
    void getByExternalId_NotFound() {
        when(feedbackRepository.findByExternalId("invalid")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> feedbackService.getByExternalId("invalid"));
    }

    @Test
    void create_Success() {
        when(securityService.getCurrentUserExternalId()).thenReturn("user-uuid-1");
        when(feedbackMapper.toEntity(testFeedbackDTO)).thenReturn(testFeedback);
        when(feedbackRepository.save(any(Feedback.class))).thenReturn(testFeedback);
        when(feedbackMapper.toDto(testFeedback)).thenReturn(testFeedbackDTO);

        FeedbackDTO result = feedbackService.create(testFeedbackDTO);

        assertNotNull(result);
        assertEquals(5, result.getRate());
        verify(feedbackRepository, times(1)).save(any(Feedback.class));
    }

    @Test
    void create_WithoutUserExternalId_SetsCurrentUser() {
        FeedbackDTO dtoWithoutUserExternalId = FeedbackDTO.builder()
                .rate(5)
                .comment("Test")
                .build();
        
        when(securityService.getCurrentUserExternalId()).thenReturn("user-uuid-1");
        when(feedbackMapper.toEntity(any(FeedbackDTO.class))).thenReturn(testFeedback);
        when(feedbackRepository.save(any(Feedback.class))).thenReturn(testFeedback);
        when(feedbackMapper.toDto(testFeedback)).thenReturn(testFeedbackDTO);

        FeedbackDTO result = feedbackService.create(dtoWithoutUserExternalId);

        assertNotNull(result);
        assertEquals("user-uuid-1", dtoWithoutUserExternalId.getUserExternalId());
    }

    @Test
    void create_Unauthorized() {
        when(securityService.isAdmin()).thenReturn(false);
        when(securityService.getCurrentUserExternalId()).thenReturn("user-uuid-1");
        testFeedbackDTO.setUserExternalId("user-uuid-2");

        assertThrows(UnauthorizedException.class, () -> feedbackService.create(testFeedbackDTO));
    }

    @Test
    void update_Success() {
        when(securityService.isAdmin()).thenReturn(true);
        when(feedbackRepository.findById(1L)).thenReturn(Optional.of(testFeedback));
        doNothing().when(feedbackMapper).updateEntityFromDto(testFeedbackDTO, testFeedback);
        when(feedbackRepository.save(any(Feedback.class))).thenReturn(testFeedback);
        when(feedbackMapper.toDto(testFeedback)).thenReturn(testFeedbackDTO);

        FeedbackDTO result = feedbackService.update(1L, testFeedbackDTO);

        assertNotNull(result);
        verify(feedbackRepository, times(1)).save(any(Feedback.class));
    }

    @Test
    void update_NotFound() {
        when(feedbackRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> feedbackService.update(999L, testFeedbackDTO));
    }

    @Test
    void update_Unauthorized() {
        when(securityService.isAdmin()).thenReturn(false);
        when(securityService.getCurrentUserExternalId()).thenReturn("user-uuid-2");
        when(feedbackRepository.findById(1L)).thenReturn(Optional.of(testFeedback));

        assertThrows(UnauthorizedException.class, () -> feedbackService.update(1L, testFeedbackDTO));
    }

    @Test
    void delete_Success() {
        when(securityService.isAdmin()).thenReturn(true);
        when(feedbackRepository.findById(1L)).thenReturn(Optional.of(testFeedback));
        doNothing().when(feedbackRepository).delete(testFeedback);

        feedbackService.delete(1L);

        verify(feedbackRepository, times(1)).delete(testFeedback);
    }

    @Test
    void delete_NotFound() {
        when(feedbackRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> feedbackService.delete(999L));
    }

    @Test
    void delete_Unauthorized() {
        when(securityService.isAdmin()).thenReturn(false);
        when(securityService.getCurrentUserExternalId()).thenReturn("user-uuid-2");
        when(feedbackRepository.findById(1L)).thenReturn(Optional.of(testFeedback));

        assertThrows(UnauthorizedException.class, () -> feedbackService.delete(1L));
    }
}








