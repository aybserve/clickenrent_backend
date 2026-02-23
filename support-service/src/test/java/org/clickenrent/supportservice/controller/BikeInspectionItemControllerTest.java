package org.clickenrent.supportservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.clickenrent.supportservice.dto.BikeInspectionItemDTO;
import org.clickenrent.supportservice.service.BikeInspectionItemService;
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

@WebMvcTest(BikeInspectionItemController.class)
@AutoConfigureMockMvc
class BikeInspectionItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BikeInspectionItemService bikeInspectionItemService;

    @MockBean
    private SecurityService securityService;

    private BikeInspectionItemDTO itemDTO;

    @BeforeEach
    void setUp() {
        itemDTO = BikeInspectionItemDTO.builder()
                .id(1L)
                .externalId("item-uuid-1")
                .bikeInspectionId(1L)
                .bikeExternalId("bike-uuid-1")
                .companyExternalId("company-uuid-1")
                .comment("Check battery")
                .bikeInspectionItemStatusId(1L)
                .bikeInspectionItemStatusName("OK")
                .errorCodeId(1L)
                .errorCodeName("E001")
                .build();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAll_ReturnsOk() throws Exception {
        when(bikeInspectionItemService.getAll()).thenReturn(Arrays.asList(itemDTO));

        mockMvc.perform(get("/api/v1/bike-inspection-items").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].bikeInspectionId").value(1));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getById_ReturnsOk() throws Exception {
        when(bikeInspectionItemService.getById(1L)).thenReturn(itemDTO);

        mockMvc.perform(get("/api/v1/bike-inspection-items/1").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.externalId").value("item-uuid-1"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getByExternalId_ReturnsOk() throws Exception {
        when(bikeInspectionItemService.getByExternalId("item-uuid-1")).thenReturn(itemDTO);

        mockMvc.perform(get("/api/v1/bike-inspection-items/external/item-uuid-1").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.externalId").value("item-uuid-1"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getByBikeInspectionId_ReturnsOk() throws Exception {
        when(bikeInspectionItemService.getByBikeInspectionId(1L)).thenReturn(Arrays.asList(itemDTO));

        mockMvc.perform(get("/api/v1/bike-inspection-items/inspection/1").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].bikeInspectionId").value(1));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getByBikeExternalId_ReturnsOk() throws Exception {
        when(bikeInspectionItemService.getByBikeExternalId("bike-uuid-1")).thenReturn(Arrays.asList(itemDTO));

        mockMvc.perform(get("/api/v1/bike-inspection-items/bike/bike-uuid-1").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].bikeExternalId").value("bike-uuid-1"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getByCompanyExternalId_ReturnsOk() throws Exception {
        when(bikeInspectionItemService.getByCompanyExternalId("company-uuid-1")).thenReturn(Arrays.asList(itemDTO));

        mockMvc.perform(get("/api/v1/bike-inspection-items/company/company-uuid-1").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].companyExternalId").value("company-uuid-1"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getByStatusId_ReturnsOk() throws Exception {
        when(bikeInspectionItemService.getByStatusId(1L)).thenReturn(Arrays.asList(itemDTO));

        mockMvc.perform(get("/api/v1/bike-inspection-items/status/1").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].bikeInspectionItemStatusId").value(1));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getByErrorCodeId_ReturnsOk() throws Exception {
        when(bikeInspectionItemService.getByErrorCodeId(1L)).thenReturn(Arrays.asList(itemDTO));

        mockMvc.perform(get("/api/v1/bike-inspection-items/error-code/1").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].errorCodeId").value(1));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void create_ReturnsCreated() throws Exception {
        when(bikeInspectionItemService.create(any())).thenReturn(itemDTO);

        mockMvc.perform(post("/api/v1/bike-inspection-items")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDTO)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void update_ReturnsOk() throws Exception {
        when(bikeInspectionItemService.update(eq(1L), any())).thenReturn(itemDTO);

        mockMvc.perform(put("/api/v1/bike-inspection-items/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDTO)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void delete_ReturnsNoContent() throws Exception {
        doNothing().when(bikeInspectionItemService).delete(1L);

        mockMvc.perform(delete("/api/v1/bike-inspection-items/1").with(csrf()))
                .andExpect(status().isNoContent());
    }
}
