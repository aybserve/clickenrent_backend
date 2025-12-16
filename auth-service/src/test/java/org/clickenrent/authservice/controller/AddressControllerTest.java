package org.clickenrent.authservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.clickenrent.authservice.config.SecurityConfig;
import org.clickenrent.authservice.dto.AddressDTO;
import org.clickenrent.authservice.service.AddressService;
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

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AddressController.class)
@AutoConfigureMockMvc
@Import(SecurityConfig.class)
@TestPropertySource(properties = "jwt.secret=dGVzdC1zZWNyZXQtZm9yLWp3dC10b2tlbi12YWxpZGF0aW9uLW11c3QtYmUtbG9uZy1lbm91Z2g=")
class AddressControllerTest {

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
    private AddressService addressService;

    private AddressDTO addressDTO;

    @BeforeEach
    void setUp() {
        addressDTO = AddressDTO.builder()
                .id(1L)
                .street("Main Street 123")
                .postcode("12345")
                .cityId(1L)
                .build();
    }

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void getAllAddresses_WithSuperadminRole_ReturnsOk() throws Exception {
        List<AddressDTO> addresses = Arrays.asList(addressDTO);
        when(addressService.getAllAddresses()).thenReturn(addresses);

        mockMvc.perform(get("/api/addresses").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].street").value("Main Street 123"));

        verify(addressService, times(1)).getAllAddresses();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllAddresses_WithAdminRole_ReturnsOk() throws Exception {
        List<AddressDTO> addresses = Arrays.asList(addressDTO);
        when(addressService.getAllAddresses()).thenReturn(addresses);

        mockMvc.perform(get("/api/addresses").with(csrf()))
                .andExpect(status().isOk());

        verify(addressService, times(1)).getAllAddresses();
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void getAllAddresses_WithCustomerRole_ReturnsForbidden() throws Exception {
        mockMvc.perform(get("/api/addresses").with(csrf()))
                .andExpect(status().isForbidden());

        verify(addressService, never()).getAllAddresses();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAddressById_ReturnsOk() throws Exception {
        when(addressService.getAddressById(1L)).thenReturn(addressDTO);

        mockMvc.perform(get("/api/addresses/1").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.street").value("Main Street 123"));

        verify(addressService, times(1)).getAddressById(1L);
    }

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void getAddressesByCityId_ReturnsOk() throws Exception {
        List<AddressDTO> addresses = Arrays.asList(addressDTO);
        when(addressService.getAddressesByCityId(1L)).thenReturn(addresses);

        mockMvc.perform(get("/api/addresses/by-city/1").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));

        verify(addressService, times(1)).getAddressesByCityId(1L);
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void getAddressesByCityId_WithCustomerRole_ReturnsForbidden() throws Exception {
        mockMvc.perform(get("/api/addresses/by-city/1").with(csrf()))
                .andExpect(status().isForbidden());

        verify(addressService, never()).getAddressesByCityId(anyLong());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void createAddress_WithCustomerRole_ReturnsCreated() throws Exception {
        AddressDTO newAddress = AddressDTO.builder().street("Oak Ave 456").postcode("67890").cityId(1L).build();
        AddressDTO createdAddress = AddressDTO.builder().id(2L).street("Oak Ave 456").postcode("67890").cityId(1L).build();

        when(addressService.createAddress(any(AddressDTO.class))).thenReturn(createdAddress);

        mockMvc.perform(post("/api/addresses")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newAddress)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(2L))
                .andExpect(jsonPath("$.street").value("Oak Ave 456"));

        verify(addressService, times(1)).createAddress(any(AddressDTO.class));
    }

    @Test
    @WithMockUser(roles = "B2B")
    void createAddress_WithB2BRole_ReturnsCreated() throws Exception {
        AddressDTO newAddress = AddressDTO.builder().street("Oak Ave 456").postcode("67890").cityId(1L).build();

        when(addressService.createAddress(any(AddressDTO.class))).thenReturn(newAddress);

        mockMvc.perform(post("/api/addresses")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newAddress)))
                .andExpect(status().isCreated());

        verify(addressService, times(1)).createAddress(any(AddressDTO.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateAddress_ReturnsOk() throws Exception {
        AddressDTO updated = AddressDTO.builder().id(1L).street("Main Street 123 Updated").postcode("12345").cityId(1L).build();

        when(addressService.updateAddress(eq(1L), any(AddressDTO.class))).thenReturn(updated);

        mockMvc.perform(put("/api/addresses/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addressDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.street").value("Main Street 123 Updated"));

        verify(addressService, times(1)).updateAddress(eq(1L), any(AddressDTO.class));
    }

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void deleteAddress_ReturnsNoContent() throws Exception {
        doNothing().when(addressService).deleteAddress(1L);

        mockMvc.perform(delete("/api/addresses/1").with(csrf()))
                .andExpect(status().isNoContent());

        verify(addressService, times(1)).deleteAddress(1L);
    }
}
