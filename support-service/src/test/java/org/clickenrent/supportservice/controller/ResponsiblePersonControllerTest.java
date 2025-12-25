package org.clickenrent.supportservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.clickenrent.supportservice.dto.ResponsiblePersonDTO;
import org.clickenrent.supportservice.service.ResponsiblePersonService;
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

@WebMvcTest(ResponsiblePersonController.class)
@AutoConfigureMockMvc
class ResponsiblePersonControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ResponsiblePersonService responsiblePersonService;

    private ResponsiblePersonDTO responsiblePersonDTO;

    @BeforeEach
    void setUp() {
        responsiblePersonDTO = ResponsiblePersonDTO.builder()
                .id(1L)
                .name("John Mechanic")
                .build();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAll_ReturnsOk() throws Exception {
        when(responsiblePersonService.getAll()).thenReturn(Arrays.asList(responsiblePersonDTO));

        mockMvc.perform(get("/api/responsible-persons").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("John Mechanic"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getById_ReturnsOk() throws Exception {
        when(responsiblePersonService.getById(1L)).thenReturn(responsiblePersonDTO);

        mockMvc.perform(get("/api/responsible-persons/1").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("John Mechanic"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void create_ReturnsCreated() throws Exception {
        when(responsiblePersonService.create(any())).thenReturn(responsiblePersonDTO);

        mockMvc.perform(post("/api/responsible-persons")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(responsiblePersonDTO)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void update_ReturnsOk() throws Exception {
        when(responsiblePersonService.update(eq(1L), any())).thenReturn(responsiblePersonDTO);

        mockMvc.perform(put("/api/responsible-persons/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(responsiblePersonDTO)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void delete_ReturnsNoContent() throws Exception {
        doNothing().when(responsiblePersonService).delete(1L);

        mockMvc.perform(delete("/api/responsible-persons/1").with(csrf()))
                .andExpect(status().isNoContent());
    }
}






