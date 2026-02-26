package org.clickenrent.supportservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.clickenrent.supportservice.dto.BikeInspectionItemPhotoDTO;
import org.clickenrent.supportservice.service.BikeInspectionItemPhotoService;
import org.clickenrent.supportservice.service.SecurityService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BikeInspectionItemPhotoController.class)
@AutoConfigureMockMvc
class BikeInspectionItemPhotoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BikeInspectionItemPhotoService bikeInspectionItemPhotoService;

    @MockBean
    private SecurityService securityService;

    private BikeInspectionItemPhotoDTO photoDTO;

    @BeforeEach
    void setUp() {
        photoDTO = BikeInspectionItemPhotoDTO.builder()
                .id(1L)
                .bikeInspectionItemId(1L)
                .photoUrl("https://example.com/photo.jpg")
                .companyExternalId("company-uuid-1")
                .build();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAll_ReturnsOk() throws Exception {
        when(bikeInspectionItemPhotoService.getAll()).thenReturn(Arrays.asList(photoDTO));

        mockMvc.perform(get("/api/v1/bike-inspection-item-photos").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].photoUrl").value("https://example.com/photo.jpg"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getById_ReturnsOk() throws Exception {
        when(bikeInspectionItemPhotoService.getById(1L)).thenReturn(photoDTO);

        mockMvc.perform(get("/api/v1/bike-inspection-item-photos/1").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bikeInspectionItemId").value(1));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getByBikeInspectionItemId_ReturnsOk() throws Exception {
        when(bikeInspectionItemPhotoService.getByBikeInspectionItemId(1L)).thenReturn(Arrays.asList(photoDTO));

        mockMvc.perform(get("/api/v1/bike-inspection-item-photos/inspection-item/1").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].bikeInspectionItemId").value(1));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getByCompanyExternalId_ReturnsOk() throws Exception {
        when(bikeInspectionItemPhotoService.getByCompanyExternalId("company-uuid-1")).thenReturn(Arrays.asList(photoDTO));

        mockMvc.perform(get("/api/v1/bike-inspection-item-photos/company/company-uuid-1").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].companyExternalId").value("company-uuid-1"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void create_ReturnsCreated() throws Exception {
        when(bikeInspectionItemPhotoService.create(any())).thenReturn(photoDTO);

        mockMvc.perform(post("/api/v1/bike-inspection-item-photos")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(photoDTO)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void update_ReturnsOk() throws Exception {
        when(bikeInspectionItemPhotoService.update(eq(1L), any())).thenReturn(photoDTO);

        mockMvc.perform(put("/api/v1/bike-inspection-item-photos/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(photoDTO)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void delete_ReturnsNoContent() throws Exception {
        doNothing().when(bikeInspectionItemPhotoService).delete(1L);

        mockMvc.perform(delete("/api/v1/bike-inspection-item-photos/1").with(csrf()))
                .andExpect(status().isNoContent());
    }
}
