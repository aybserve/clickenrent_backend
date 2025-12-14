package org.clickenrent.authservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.clickenrent.authservice.dto.*;
import org.clickenrent.authservice.service.InvitationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(InvitationController.class)
@AutoConfigureMockMvc
class InvitationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private InvitationService invitationService;

    private InvitationDTO invitationDTO;
    private CreateInvitationRequest createRequest;
    private CompleteInvitationRequest completeRequest;
    private AuthResponse authResponse;

    @BeforeEach
    void setUp() {
        invitationDTO = InvitationDTO.builder()
                .id(1L)
                .email("invited@example.com")
                .token("invitation-token-123")
                .companyId(1L)
                .build();

        createRequest = CreateInvitationRequest.builder()
                .email("invited@example.com")
                .companyId(1L)
                .build();

        completeRequest = CompleteInvitationRequest.builder()
                .token("invitation-token-123")
                .userName("newb2buser")
                .password("password123")
                .firstName("New")
                .lastName("B2B User")
                .build();

        UserDTO userDTO = UserDTO.builder()
                .id(1L)
                .userName("newb2buser")
                .email("invited@example.com")
                .isActive(true)
                .build();

        authResponse = AuthResponse.builder()
                .accessToken("accessToken123")
                .refreshToken("refreshToken123")
                .expiresIn(3600000L)
                .user(userDTO)
                .build();
    }

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void createInvitation_WithSuperadminRole_ReturnsCreated() throws Exception {
        when(invitationService.createInvitation(any(CreateInvitationRequest.class))).thenReturn(invitationDTO);

        mockMvc.perform(post("/api/invitations")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.email").value("invited@example.com"));

        verify(invitationService, times(1)).createInvitation(any(CreateInvitationRequest.class));
    }

    @Test
    @WithMockUser(roles = "B2B")
    void createInvitation_WithB2BRole_ReturnsCreated() throws Exception {
        when(invitationService.createInvitation(any(CreateInvitationRequest.class))).thenReturn(invitationDTO);

        mockMvc.perform(post("/api/invitations")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated());

        verify(invitationService, times(1)).createInvitation(any(CreateInvitationRequest.class));
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void createInvitation_WithCustomerRole_ReturnsForbidden() throws Exception {
        mockMvc.perform(post("/api/invitations")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isForbidden());

        verify(invitationService, never()).createInvitation(any(CreateInvitationRequest.class));
    }

    @Test
    void validateToken_WithValidToken_ReturnsOk() throws Exception {
        when(invitationService.validateToken("invitation-token-123")).thenReturn(invitationDTO);

        mockMvc.perform(get("/api/invitations/validate/invitation-token-123")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("invitation-token-123"))
                .andExpect(jsonPath("$.email").value("invited@example.com"));

        verify(invitationService, times(1)).validateToken("invitation-token-123");
    }

    @Test
    void validateToken_NoAuthRequired() throws Exception {
        when(invitationService.validateToken(anyString())).thenReturn(invitationDTO);

        // Should work without authentication
        mockMvc.perform(get("/api/invitations/validate/some-token")
                        .with(csrf()))
                .andExpect(status().isOk());

        verify(invitationService, times(1)).validateToken(anyString());
    }

    @Test
    void completeInvitation_WithValidRequest_ReturnsCreated() throws Exception {
        when(invitationService.completeInvitation(any(CompleteInvitationRequest.class))).thenReturn(authResponse);

        mockMvc.perform(post("/api/invitations/complete")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(completeRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accessToken").value("accessToken123"))
                .andExpect(jsonPath("$.user.userName").value("newb2buser"));

        verify(invitationService, times(1)).completeInvitation(any(CompleteInvitationRequest.class));
    }

    @Test
    void completeInvitation_NoAuthRequired() throws Exception {
        when(invitationService.completeInvitation(any(CompleteInvitationRequest.class))).thenReturn(authResponse);

        // Should work without authentication
        mockMvc.perform(post("/api/invitations/complete")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(completeRequest)))
                .andExpect(status().isCreated());

        verify(invitationService, times(1)).completeInvitation(any(CompleteInvitationRequest.class));
    }

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void getAllInvitations_WithSuperadminRole_ReturnsOk() throws Exception {
        List<InvitationDTO> invitations = Arrays.asList(invitationDTO);
        when(invitationService.getAllInvitations()).thenReturn(invitations);

        mockMvc.perform(get("/api/invitations")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].email").value("invited@example.com"));

        verify(invitationService, times(1)).getAllInvitations();
    }

    @Test
    @WithMockUser(roles = "B2B")
    void getAllInvitations_WithB2BRole_ReturnsOk() throws Exception {
        List<InvitationDTO> invitations = Arrays.asList(invitationDTO);
        when(invitationService.getAllInvitations()).thenReturn(invitations);

        mockMvc.perform(get("/api/invitations")
                        .with(csrf()))
                .andExpect(status().isOk());

        verify(invitationService, times(1)).getAllInvitations();
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void getAllInvitations_WithCustomerRole_ReturnsForbidden() throws Exception {
        mockMvc.perform(get("/api/invitations")
                        .with(csrf()))
                .andExpect(status().isForbidden());

        verify(invitationService, never()).getAllInvitations();
    }

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void cancelInvitation_WithSuperadminRole_ReturnsNoContent() throws Exception {
        doNothing().when(invitationService).cancelInvitation(1L);

        mockMvc.perform(delete("/api/invitations/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(invitationService, times(1)).cancelInvitation(1L);
    }

    @Test
    @WithMockUser(roles = "B2B")
    void cancelInvitation_WithB2BRole_ReturnsNoContent() throws Exception {
        doNothing().when(invitationService).cancelInvitation(1L);

        mockMvc.perform(delete("/api/invitations/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(invitationService, times(1)).cancelInvitation(1L);
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void cancelInvitation_WithCustomerRole_ReturnsForbidden() throws Exception {
        mockMvc.perform(delete("/api/invitations/1")
                        .with(csrf()))
                .andExpect(status().isForbidden());

        verify(invitationService, never()).cancelInvitation(anyLong());
    }
}
