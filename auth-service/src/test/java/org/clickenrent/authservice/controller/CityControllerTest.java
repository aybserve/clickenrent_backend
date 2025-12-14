package org.clickenrent.authservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.clickenrent.authservice.dto.CityDTO;
import org.clickenrent.authservice.service.CityService;
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

@WebMvcTest(CityController.class)
@AutoConfigureMockMvc
class CityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CityService cityService;

    private CityDTO cityDTO;

    @BeforeEach
    void setUp() {
        cityDTO = CityDTO.builder()
                .id(1L)
                .name("Berlin")
                .countryId(1L)
                .build();
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void getAllCities_WithCustomerRole_ReturnsOk() throws Exception {
        List<CityDTO> cities = Arrays.asList(cityDTO);
        when(cityService.getAllCities()).thenReturn(cities);

        mockMvc.perform(get("/api/cities").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Berlin"));

        verify(cityService, times(1)).getAllCities();
    }

    @Test
    @WithMockUser(roles = "B2B")
    void getCityById_ReturnsOk() throws Exception {
        when(cityService.getCityById(1L)).thenReturn(cityDTO);

        mockMvc.perform(get("/api/cities/1").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Berlin"));

        verify(cityService, times(1)).getCityById(1L);
    }

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void createCity_WithValidRequest_ReturnsCreated() throws Exception {
        CityDTO newCity = CityDTO.builder().name("Munich").countryId(1L).build();
        CityDTO createdCity = CityDTO.builder().id(2L).name("Munich").countryId(1L).build();

        when(cityService.createCity(any(CityDTO.class))).thenReturn(createdCity);

        mockMvc.perform(post("/api/cities")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newCity)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(2L))
                .andExpect(jsonPath("$.name").value("Munich"));

        verify(cityService, times(1)).createCity(any(CityDTO.class));
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void createCity_WithCustomerRole_ReturnsForbidden() throws Exception {
        CityDTO newCity = CityDTO.builder().name("Munich").countryId(1L).build();

        mockMvc.perform(post("/api/cities")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newCity)))
                .andExpect(status().isForbidden());

        verify(cityService, never()).createCity(any(CityDTO.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateCity_ReturnsOk() throws Exception {
        CityDTO updated = CityDTO.builder().id(1L).name("Berlin (Updated)").countryId(1L).build();

        when(cityService.updateCity(eq(1L), any(CityDTO.class))).thenReturn(updated);

        mockMvc.perform(put("/api/cities/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cityDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Berlin (Updated)"));

        verify(cityService, times(1)).updateCity(eq(1L), any(CityDTO.class));
    }

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void deleteCity_ReturnsNoContent() throws Exception {
        doNothing().when(cityService).deleteCity(1L);

        mockMvc.perform(delete("/api/cities/1").with(csrf()))
                .andExpect(status().isNoContent());

        verify(cityService, times(1)).deleteCity(1L);
    }
}
