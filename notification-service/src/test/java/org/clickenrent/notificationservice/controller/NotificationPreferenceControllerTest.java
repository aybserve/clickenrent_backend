package org.clickenrent.notificationservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.clickenrent.contracts.notification.NotificationPreferenceDTO;
import org.clickenrent.contracts.notification.UpdatePreferencesRequest;
import org.clickenrent.notificationservice.config.SecurityConfig;
import org.clickenrent.notificationservice.service.NotificationPreferenceService;
import org.clickenrent.notificationservice.service.SecurityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(NotificationPreferenceController.class)
@Import(SecurityConfig.class)
@TestPropertySource(properties = "jwt.secret=dGVzdC1zZWNyZXQtZm9yLWp3dC10b2tlbi12YWxpZGF0aW9uLW11c3QtYmUtbG9uZy1lbm91Z2g=")
class NotificationPreferenceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private NotificationPreferenceService preferenceService;

    @MockBean
    private SecurityService securityService;

    private static final String USER_ID = "test-user-ext-id";

    @BeforeEach
    void setUp() {
        when(securityService.getCurrentUserExternalId()).thenReturn(USER_ID);
    }

    @Test
    @WithMockUser
    void getPreferences_returnsOk() throws Exception {
        NotificationPreferenceDTO dto = NotificationPreferenceDTO.builder()
                .id(1L)
                .userExternalId(USER_ID)
                .rentalUpdatesEnabled(true)
                .rentalStartEnabled(true)
                .rentalEndRemindersEnabled(true)
                .rentalCompletionEnabled(true)
                .paymentUpdatesEnabled(true)
                .supportMessagesEnabled(true)
                .marketingEnabled(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        when(preferenceService.getPreferences(USER_ID)).thenReturn(dto);

        mockMvc.perform(get("/api/v1/notifications/preferences")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userExternalId").value(USER_ID))
                .andExpect(jsonPath("$.rentalUpdatesEnabled").value(true))
                .andExpect(jsonPath("$.marketingEnabled").value(false));

        verify(preferenceService).getPreferences(USER_ID);
    }

    @Test
    @WithMockUser
    void updatePreferences_returnsOk() throws Exception {
        UpdatePreferencesRequest request = UpdatePreferencesRequest.builder()
                .marketingEnabled(true)
                .rentalEndRemindersEnabled(false)
                .build();

        NotificationPreferenceDTO dto = NotificationPreferenceDTO.builder()
                .id(1L)
                .userExternalId(USER_ID)
                .rentalUpdatesEnabled(true)
                .rentalStartEnabled(true)
                .rentalEndRemindersEnabled(false)
                .rentalCompletionEnabled(true)
                .paymentUpdatesEnabled(true)
                .supportMessagesEnabled(true)
                .marketingEnabled(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        when(preferenceService.updatePreferences(eq(USER_ID), any(UpdatePreferencesRequest.class))).thenReturn(dto);

        mockMvc.perform(put("/api/v1/notifications/preferences")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.marketingEnabled").value(true))
                .andExpect(jsonPath("$.rentalEndRemindersEnabled").value(false));

        verify(preferenceService).updatePreferences(eq(USER_ID), any(UpdatePreferencesRequest.class));
    }
}
