package org.clickenrent.rentalservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.clickenrent.rentalservice.dto.HubDTO;
import org.clickenrent.rentalservice.service.HubService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HubController.class)
@AutoConfigureMockMvc
class HubControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private HubService hubService;

    private HubDTO hubDTO;

    @BeforeEach
    void setUp() {
        hubDTO = HubDTO.builder()
                .id(1L)
                .externalId("HUB001")
                .name("Main Hub")
                .locationId(1L)
                .build();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllHubs_WithAdminRole_ReturnsOk() throws Exception {
        Page<HubDTO> page = new PageImpl<>(Collections.singletonList(hubDTO));
        when(hubService.getAllHubs(any())).thenReturn(page);

        mockMvc.perform(get("/api/hubs").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Main Hub"));

        verify(hubService, times(1)).getAllHubs(any());
    }
    @Test
    @WithMockUser(roles = "CUSTOMER")
    void getAllHubs_WithCustomerRole_ReturnsOk() throws Exception {
        Page<HubDTO> page = new PageImpl<>(Collections.singletonList(hubDTO));
        when(hubService.getAllHubs(any())).thenReturn(page);

        mockMvc.perform(get("/api/hubs").with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getHubsByLocation_ReturnsOk() throws Exception {
        when(hubService.getHubsByLocation(1L)).thenReturn(Arrays.asList(hubDTO));

        mockMvc.perform(get("/api/hubs/by-location/1").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    void getHubById_ReturnsOk() throws Exception {
        when(hubService.getHubById(1L)).thenReturn(hubDTO);

        mockMvc.perform(get("/api/hubs/1").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Main Hub"));
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    void createHub_ReturnsCreated() throws Exception {
        when(hubService.createHub(any())).thenReturn(hubDTO);

        mockMvc.perform(post("/api/hubs")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(hubDTO)))
                .andExpect(status().isCreated());
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    void updateHub_ReturnsOk() throws Exception {
        when(hubService.updateHub(eq(1L), any())).thenReturn(hubDTO);

        mockMvc.perform(put("/api/hubs/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(hubDTO)))
                .andExpect(status().isOk());
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteHub_ReturnsNoContent() throws Exception {
        doNothing().when(hubService).deleteHub(1L);

        mockMvc.perform(delete("/api/hubs/1").with(csrf()))
                .andExpect(status().isNoContent());
}
}

