package org.clickenrent.authservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.clickenrent.authservice.dto.UserAddressDTO;
import org.clickenrent.authservice.config.SecurityConfig;
import org.clickenrent.authservice.service.UserAddressService;
import org.clickenrent.authservice.config.SecurityConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.TestPropertySource;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserAddressController.class)
@AutoConfigureMockMvc
@Import(SecurityConfig.class)
@TestPropertySource(properties = "jwt.secret=dGVzdC1zZWNyZXQtZm9yLWp3dC10b2tlbi12YWxpZGF0aW9uLW11c3QtYmUtbG9uZy1lbm91Z2g=")
class UserAddressControllerTest {

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
    private UserAddressService userAddressService;

    @MockBean
    private org.clickenrent.authservice.service.SecurityService securityService;

    @MockBean(name = "resourceSecurity")
    private org.clickenrent.authservice.security.ResourceSecurityExpression resourceSecurity;

    private UserAddressDTO userAddressDTO;

    @BeforeEach
    void setUp() {
        userAddressDTO = UserAddressDTO.builder()
                .id(1L)
                .userId(1L)
                .addressId(1L)
                .build();
    }

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void getAllUserAddresses_WithSuperadminRole_ReturnsOk() throws Exception {
        List<UserAddressDTO> userAddresses = Arrays.asList(userAddressDTO);
        when(userAddressService.getAllUserAddresses()).thenReturn(userAddresses);

        mockMvc.perform(get("/api/v1/user-addresses").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].userId").value(1L));

        verify(userAddressService, times(1)).getAllUserAddresses();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllUserAddresses_WithAdminRole_ReturnsOk() throws Exception {
        List<UserAddressDTO> userAddresses = Arrays.asList(userAddressDTO);
        when(userAddressService.getAllUserAddresses()).thenReturn(userAddresses);

        mockMvc.perform(get("/api/v1/user-addresses").with(csrf()))
                .andExpect(status().isOk());

        verify(userAddressService, times(1)).getAllUserAddresses();
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void getAllUserAddresses_WithCustomerRole_ReturnsForbidden() throws Exception {
        mockMvc.perform(get("/api/v1/user-addresses").with(csrf()))
                .andExpect(status().isForbidden());

        verify(userAddressService, never()).getAllUserAddresses();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getUserAddressById_ReturnsOk() throws Exception {
        when(userAddressService.getUserAddressById(1L)).thenReturn(userAddressDTO);

        mockMvc.perform(get("/api/v1/user-addresses/1").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));

        verify(userAddressService, times(1)).getUserAddressById(1L);
    }

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void getUserAddressesByUserId_ReturnsOk() throws Exception {
        List<UserAddressDTO> userAddresses = Arrays.asList(userAddressDTO);
        when(userAddressService.getUserAddressesByUserId(1L)).thenReturn(userAddresses);

        mockMvc.perform(get("/api/v1/user-addresses/by-user/1").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userId").value(1L));

        verify(userAddressService, times(1)).getUserAddressesByUserId(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getUserAddressesByAddressId_ReturnsOk() throws Exception {
        List<UserAddressDTO> userAddresses = Arrays.asList(userAddressDTO);
        when(userAddressService.getUserAddressesByAddressId(1L)).thenReturn(userAddresses);

        mockMvc.perform(get("/api/v1/user-addresses/by-address/1").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].addressId").value(1L));

        verify(userAddressService, times(1)).getUserAddressesByAddressId(1L);
    }

    @Test
    @WithMockUser(roles = "B2B")
    void createUserAddress_WithB2BRole_ReturnsCreated() throws Exception {
        UserAddressDTO newUserAddress = UserAddressDTO.builder().userId(1L).addressId(2L).build();
        UserAddressDTO createdUserAddress = UserAddressDTO.builder().id(2L).userId(1L).addressId(2L).build();

        when(userAddressService.createUserAddress(any(UserAddressDTO.class))).thenReturn(createdUserAddress);

        mockMvc.perform(post("/api/v1/user-addresses")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUserAddress)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(2L));

        verify(userAddressService, times(1)).createUserAddress(any(UserAddressDTO.class));
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void createUserAddress_WithCustomerRole_ReturnsForbidden() throws Exception {
        UserAddressDTO newUserAddress = UserAddressDTO.builder().userId(1L).addressId(2L).build();

        mockMvc.perform(post("/api/v1/user-addresses")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUserAddress)))
                .andExpect(status().isForbidden());

        verify(userAddressService, never()).createUserAddress(any(UserAddressDTO.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateUserAddress_ReturnsOk() throws Exception {
        UserAddressDTO updated = UserAddressDTO.builder().id(1L).userId(1L).addressId(3L).build();

        when(userAddressService.updateUserAddress(eq(1L), any(UserAddressDTO.class))).thenReturn(updated);

        mockMvc.perform(put("/api/v1/user-addresses/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userAddressDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.addressId").value(3L));

        verify(userAddressService, times(1)).updateUserAddress(eq(1L), any(UserAddressDTO.class));
    }

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void deleteUserAddress_ReturnsNoContent() throws Exception {
        doNothing().when(userAddressService).deleteUserAddress(1L);

        mockMvc.perform(delete("/api/v1/user-addresses/1").with(csrf()))
                .andExpect(status().isNoContent());

        verify(userAddressService, times(1)).deleteUserAddress(1L);
    }
}
