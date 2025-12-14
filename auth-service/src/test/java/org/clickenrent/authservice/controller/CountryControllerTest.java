package org.clickenrent.authservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.clickenrent.authservice.dto.CountryDTO;
import org.clickenrent.authservice.service.CountryService;
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

@WebMvcTest(CountryController.class)
@AutoConfigureMockMvc
class CountryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CountryService countryService;

    private CountryDTO countryDTO;

    @BeforeEach
    void setUp() {
        countryDTO = CountryDTO.builder()
                .id(1L)
                .name("Germany")
                .build();
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void getAllCountries_WithCustomerRole_ReturnsOk() throws Exception {
        List<CountryDTO> countries = Arrays.asList(countryDTO);
        when(countryService.getAllCountries()).thenReturn(countries);

        mockMvc.perform(get("/api/countries").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Germany"));

        verify(countryService, times(1)).getAllCountries();
    }

    @Test
    @WithMockUser(roles = "B2B")
    void getCountryById_ReturnsOk() throws Exception {
        when(countryService.getCountryById(1L)).thenReturn(countryDTO);

        mockMvc.perform(get("/api/countries/1").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Germany"));

        verify(countryService, times(1)).getCountryById(1L);
    }

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void createCountry_WithValidRequest_ReturnsCreated() throws Exception {
        CountryDTO newCountry = CountryDTO.builder().name("France").build();
        CountryDTO createdCountry = CountryDTO.builder().id(2L).name("France").build();

        when(countryService.createCountry(any(CountryDTO.class))).thenReturn(createdCountry);

        mockMvc.perform(post("/api/countries")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newCountry)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(2L))
                .andExpect(jsonPath("$.name").value("France"));

        verify(countryService, times(1)).createCountry(any(CountryDTO.class));
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void createCountry_WithCustomerRole_ReturnsForbidden() throws Exception {
        CountryDTO newCountry = CountryDTO.builder().name("France").build();

        mockMvc.perform(post("/api/countries")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newCountry)))
                .andExpect(status().isForbidden());

        verify(countryService, never()).createCountry(any(CountryDTO.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateCountry_ReturnsOk() throws Exception {
        CountryDTO updated = CountryDTO.builder().id(1L).name("Germany (Updated)").build();

        when(countryService.updateCountry(eq(1L), any(CountryDTO.class))).thenReturn(updated);

        mockMvc.perform(put("/api/countries/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(countryDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Germany (Updated)"));

        verify(countryService, times(1)).updateCountry(eq(1L), any(CountryDTO.class));
    }

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void deleteCountry_ReturnsNoContent() throws Exception {
        doNothing().when(countryService).deleteCountry(1L);

        mockMvc.perform(delete("/api/countries/1").with(csrf()))
                .andExpect(status().isNoContent());

        verify(countryService, times(1)).deleteCountry(1L);
    }
}
