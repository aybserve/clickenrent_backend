package org.clickenrent.notificationservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.clickenrent.contracts.notification.*;
import org.clickenrent.notificationservice.entity.NotificationLog;
import org.clickenrent.notificationservice.repository.NotificationLogRepository;
import org.clickenrent.notificationservice.service.NotificationService;
import org.clickenrent.notificationservice.service.SecurityService;
import org.clickenrent.notificationservice.service.TokenManagementService;
import org.clickenrent.notificationservice.config.SecurityConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(NotificationController.class)
@Import(SecurityConfig.class)
@TestPropertySource(properties = "jwt.secret=dGVzdC1zZWNyZXQtZm9yLWp3dC10b2tlbi12YWxpZGF0aW9uLW11c3QtYmUtbG9uZy1lbm91Z2g=")
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private NotificationService notificationService;

    @MockBean
    private TokenManagementService tokenManagementService;

    @MockBean
    private SecurityService securityService;

    @MockBean
    private NotificationLogRepository notificationLogRepository;

    private static final String USER_ID = "test-user-ext-id";

    @BeforeEach
    void setUp() {
        when(securityService.getCurrentUserExternalId()).thenReturn(USER_ID);
    }

    @Test
    @WithMockUser
    void registerToken_returnsOk() throws Exception {
        RegisterTokenRequest request = RegisterTokenRequest.builder()
                .expoPushToken("ExponentPushToken[abc]")
                .platform("ios")
                .deviceId("device-1")
                .appVersion("1.0.0")
                .build();

        RegisterTokenResponse response = RegisterTokenResponse.builder()
                .success(true)
                .notificationId("1")
                .deviceId("device-1")
                .registeredAt(LocalDateTime.now())
                .build();
        when(tokenManagementService.registerToken(eq(USER_ID), any(RegisterTokenRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/notifications/register-token")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.deviceId").value("device-1"));

        verify(tokenManagementService).registerToken(eq(USER_ID), any(RegisterTokenRequest.class));
    }

    @Test
    @WithMockUser
    void sendNotification_internal_returnsOk() throws Exception {
        SendNotificationRequest request = SendNotificationRequest.builder()
                .userExternalId("user-1")
                .notificationType("BIKE_UNLOCKED")
                .title("Bike unlocked")
                .body("Your bike is ready.")
                .build();

        SendNotificationResponse response = SendNotificationResponse.builder()
                .success(true)
                .receiptId("rec-1")
                .build();
        when(notificationService.send(any(SendNotificationRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/notifications/internal/send")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.receiptId").value("rec-1"));

        verify(notificationService).send(any(SendNotificationRequest.class));
    }

    @Test
    @WithMockUser
    void getHistory_returnsOkWithPagination() throws Exception {
        NotificationLog log = NotificationLog.builder()
                .id(1L)
                .userExternalId(USER_ID)
                .notificationType("BIKE_UNLOCKED")
                .title("Title")
                .body("Body")
                .status("sent")
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();
        Page<NotificationLog> page = new PageImpl<>(List.of(log), org.springframework.data.domain.PageRequest.of(0, 20), 1);
        when(notificationLogRepository.findByUserExternalId(eq(USER_ID), any())).thenReturn(page);
        when(notificationLogRepository.countByUserExternalIdAndIsReadFalse(USER_ID)).thenReturn(1L);

        mockMvc.perform(get("/api/v1/notifications/history")
                        .with(csrf())
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(1))
                .andExpect(jsonPath("$.unreadCount").value(1))
                .andExpect(jsonPath("$.notifications[0].title").value("Title"));
    }

    @Test
    @WithMockUser
    void deleteToken_returnsOk() throws Exception {
        String token = "ExponentPushToken_abc123";
        mockMvc.perform(delete("/api/v1/notifications/tokens/" + token)
                        .with(csrf()))
                .andExpect(status().isOk());

        verify(tokenManagementService).deleteToken(eq(USER_ID), eq(token));
    }

    @Test
    @WithMockUser
    void markAsRead_whenSuccess_returnsOk() throws Exception {
        when(notificationService.markAsRead("1", USER_ID)).thenReturn(true);

        mockMvc.perform(post("/api/v1/notifications/1/read")
                        .with(csrf()))
                .andExpect(status().isOk());

        verify(notificationService).markAsRead("1", USER_ID);
    }

    @Test
    @WithMockUser
    void markAsRead_whenNotFound_returns404() throws Exception {
        when(notificationService.markAsRead("999", USER_ID)).thenReturn(false);

        mockMvc.perform(post("/api/v1/notifications/999/read")
                        .with(csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void markAllAsRead_returnsOk() throws Exception {
        when(notificationService.markAllAsRead(USER_ID)).thenReturn(3);

        mockMvc.perform(post("/api/v1/notifications/read-all")
                        .with(csrf()))
                .andExpect(status().isOk());

        verify(notificationService).markAllAsRead(USER_ID);
    }
}
