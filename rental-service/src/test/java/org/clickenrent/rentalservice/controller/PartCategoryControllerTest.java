package org.clickenrent.rentalservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.clickenrent.rentalservice.dto.PartCategoryDTO;
import org.clickenrent.rentalservice.service.PartCategoryService;
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

import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PartCategoryController.class)
@AutoConfigureMockMvc
class PartCategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PartCategoryService partCategoryService;

    private PartCategoryDTO categoryDTO;

    @BeforeEach
    void setUp() {
        categoryDTO = PartCategoryDTO.builder()
                .id(1L)
                .externalId("PC001")
                .name("Battery")
                .build();
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void getAllCategories_ReturnsOk() throws Exception {
        Page<PartCategoryDTO> page = new PageImpl<>(Collections.singletonList(categoryDTO));
        when(partCategoryService.getAllCategories(any())).thenReturn(page);

        mockMvc.perform(get("/api/part-categories").with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getCategoryById_ReturnsOk() throws Exception {
        when(partCategoryService.getCategoryById(1L)).thenReturn(categoryDTO);

        mockMvc.perform(get("/api/part-categories/1").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Battery"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createCategory_ReturnsCreated() throws Exception {
        when(partCategoryService.createCategory(any())).thenReturn(categoryDTO);

        mockMvc.perform(post("/api/part-categories")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryDTO)))
                .andExpect(status().isCreated());
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    void updateCategory_ReturnsOk() throws Exception {
        when(partCategoryService.updateCategory(eq(1L), any())).thenReturn(categoryDTO);

        mockMvc.perform(put("/api/part-categories/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryDTO)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteCategory_ReturnsNoContent() throws Exception {
        doNothing().when(partCategoryService).deleteCategory(1L);

        mockMvc.perform(delete("/api/part-categories/1").with(csrf()))
                .andExpect(status().isNoContent());
    }
}








