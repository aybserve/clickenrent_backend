package org.clickenrent.authservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.clickenrent.authservice.config.SecurityConfig;
import org.clickenrent.authservice.dto.UpdateUserPreferenceRequest;
import org.clickenrent.authservice.dto.UserPreferenceDTO;
import org.clickenrent.authservice.service.UserPreferenceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for UserPreferenceController.
 */
@WebMvcTest(UserPreferenceController.class)
@AutoConfigureMockMvc
@Import(SecurityConfig.class)
@TestPropertySource(properties = "jwt.secret=dGVzdC1zZWNyZXQtZm9yLWp3dC10b2tlbi12YWxpZGF0aW9uLW11c3QtYmUtbG9uZy1lbm91Z2g=")
class UserPreferenceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private org.clickenrent.authservice.service.CustomUserDetailsService customUserDetailsService;

    @MockBean
    private org.clickenrent.authservice.service.JwtService jwtService;

    @MockBean
    private org.clickenrent.authservice.service.TokenBlacklistService tokenBlacklistService;

    @MockBean
    private org.clickenrent.authservice.security.ResourceSecurityExpression resourceSecurity;

    @MockBean
    private UserPreferenceService userPreferenceService;

    private UserPreferenceDTO userPreferenceDTO;
    private UpdateUserPreferenceRequest updateRequest;

    @BeforeEach
    void setUp() {
        Map<String, List<String>> navigationOrder = new HashMap<>();
        navigationOrder.put("customer", List.of("rentals", "bikes", "profile"));

        userPreferenceDTO = UserPreferenceDTO.builder()
                .id(1L)
                .userId(1L)
                .userExternalId("usr-ext-00001")
                .navigationOrder(navigationOrder)
                .theme("system")
                .languageId(1L)
                .languageName("English")
                .timezone("UTC")
                .dateFormat("YYYY-MM-DD")
                .timeFormat("24h")
                .currency("USD")
                .emailNotifications(true)
                .pushNotifications(true)
                .smsNotifications(false)
                .notificationFrequency("immediate")
                .itemsPerPage(20)
                .dashboardLayout(new HashMap<>())
                .tablePreferences(new HashMap<>())
                .defaultFilters(new HashMap<>())
                .build();

        updateRequest = UpdateUserPreferenceRequest.builder()
                .theme("dark")
                .languageId(4L)  // Spanish
                .itemsPerPage(50)
                .build();
    }

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void getUserPreferences_WithSuperadminRole_ReturnsOk() throws Exception {
        // Given
        when(userPreferenceService.getUserPreferences(1L)).thenReturn(userPreferenceDTO);

        // When & Then
        mockMvc.perform(get("/api/v1/users/1/preferences")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.theme").value("system"))
                .andExpect(jsonPath("$.languageId").value(1))
                .andExpect(jsonPath("$.languageName").value("English"));

        verify(userPreferenceService).getUserPreferences(1L);
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void getUserPreferences_AsCustomer_WithOwnPreferences_ReturnsOk() throws Exception {
        // Given
        when(resourceSecurity.canAccessUser(1L)).thenReturn(true);
        when(userPreferenceService.getUserPreferences(1L)).thenReturn(userPreferenceDTO);

        // When & Then
        mockMvc.perform(get("/api/v1/users/1/preferences")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1));

        verify(userPreferenceService).getUserPreferences(1L);
    }

    @Test
    void getUserPreferencesByExternalId_ReturnsOk() throws Exception {
        // Given
        when(userPreferenceService.getUserPreferencesByExternalId("usr-ext-00001"))
                .thenReturn(userPreferenceDTO);

        // When & Then
        mockMvc.perform(get("/api/v1/users/external/usr-ext-00001/preferences"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userExternalId").value("usr-ext-00001"));

        verify(userPreferenceService).getUserPreferencesByExternalId("usr-ext-00001");
    }

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void updateUserPreferences_WithValidRequest_ReturnsOk() throws Exception {
        // Given
        UserPreferenceDTO updatedDTO = UserPreferenceDTO.builder()
                .id(1L)
                .userId(1L)
                .theme("dark")
                .languageId(4L)
                .languageName("Spanish")
                .itemsPerPage(50)
                .build();

        when(userPreferenceService.updateUserPreferences(eq(1L), any(UpdateUserPreferenceRequest.class)))
                .thenReturn(updatedDTO);

        // When & Then
        mockMvc.perform(put("/api/v1/users/1/preferences")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.theme").value("dark"))
                .andExpect(jsonPath("$.languageId").value(4))
                .andExpect(jsonPath("$.languageName").value("Spanish"))
                .andExpect(jsonPath("$.itemsPerPage").value(50));

        verify(userPreferenceService).updateUserPreferences(eq(1L), any(UpdateUserPreferenceRequest.class));
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void updateUserPreferences_AsCustomer_WithOwnPreferences_ReturnsOk() throws Exception {
        // Given
        when(resourceSecurity.canAccessUser(1L)).thenReturn(true);
        when(userPreferenceService.updateUserPreferences(eq(1L), any(UpdateUserPreferenceRequest.class)))
                .thenReturn(userPreferenceDTO);

        // When & Then
        mockMvc.perform(put("/api/v1/users/1/preferences")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk());

        verify(userPreferenceService).updateUserPreferences(eq(1L), any(UpdateUserPreferenceRequest.class));
    }

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void updateUserPreferences_WithInvalidTheme_ReturnsBadRequest() throws Exception {
        // Given
        UpdateUserPreferenceRequest invalidRequest = UpdateUserPreferenceRequest.builder()
                .theme("invalid-theme")
                .build();

        // When & Then
        mockMvc.perform(put("/api/v1/users/1/preferences")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(userPreferenceService, never()).updateUserPreferences(anyLong(), any());
    }

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void updateUserPreferences_WithInvalidItemsPerPage_ReturnsBadRequest() throws Exception {
        // Given
        UpdateUserPreferenceRequest invalidRequest = UpdateUserPreferenceRequest.builder()
                .itemsPerPage(150) // exceeds max of 100
                .build();

        // When & Then
        mockMvc.perform(put("/api/v1/users/1/preferences")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(userPreferenceService, never()).updateUserPreferences(anyLong(), any());
    }

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void resetUserPreferences_ReturnsOk() throws Exception {
        // Given
        when(userPreferenceService.resetToDefaults(1L)).thenReturn(userPreferenceDTO);

        // When & Then
        mockMvc.perform(post("/api/v1/users/1/preferences/reset")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.theme").value("system"));

        verify(userPreferenceService).resetToDefaults(1L);
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void resetUserPreferences_AsCustomer_WithOwnPreferences_ReturnsOk() throws Exception {
        // Given
        when(resourceSecurity.canAccessUser(1L)).thenReturn(true);
        when(userPreferenceService.resetToDefaults(1L)).thenReturn(userPreferenceDTO);

        // When & Then
        mockMvc.perform(post("/api/v1/users/1/preferences/reset")
                        .with(csrf()))
                .andExpect(status().isOk());

        verify(userPreferenceService).resetToDefaults(1L);
    }

    @Test
    void getUserPreferences_WithoutAuthentication_ReturnsUnauthorized() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/users/1/preferences"))
                .andExpect(status().isUnauthorized());

        verify(userPreferenceService, never()).getUserPreferences(anyLong());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void updateUserPreferences_AsCustomer_WithOthersPreferences_ReturnsForbidden() throws Exception {
        // Given
        when(resourceSecurity.canAccessUser(2L)).thenReturn(false);

        // When & Then
        mockMvc.perform(put("/api/v1/users/2/preferences")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isForbidden());

        verify(userPreferenceService, never()).updateUserPreferences(anyLong(), any());
    }
}
