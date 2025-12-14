package org.clickenrent.authservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.clickenrent.authservice.dto.LanguageDTO;
import org.clickenrent.authservice.service.LanguageService;
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

@WebMvcTest(LanguageController.class)
@AutoConfigureMockMvc
class LanguageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private LanguageService languageService;

    private LanguageDTO languageDTO;

    @BeforeEach
    void setUp() {
        languageDTO = LanguageDTO.builder()
                .id(1L)
                .name("English")
                .build();
    }

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void getAllLanguages_WithSuperadminRole_ReturnsOk() throws Exception {
        List<LanguageDTO> languages = Arrays.asList(languageDTO);
        when(languageService.getAllLanguages()).thenReturn(languages);

        mockMvc.perform(get("/api/languages").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("English"));

        verify(languageService, times(1)).getAllLanguages();
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void getAllLanguages_WithCustomerRole_ReturnsOk() throws Exception {
        List<LanguageDTO> languages = Arrays.asList(languageDTO);
        when(languageService.getAllLanguages()).thenReturn(languages);

        mockMvc.perform(get("/api/languages").with(csrf()))
                .andExpect(status().isOk());

        verify(languageService, times(1)).getAllLanguages();
    }

    @Test
    void getAllLanguages_WithoutAuthentication_ReturnsForbidden() throws Exception {
        mockMvc.perform(get("/api/languages").with(csrf()))
                .andExpect(status().isForbidden());

        verify(languageService, never()).getAllLanguages();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getLanguageById_ReturnsOk() throws Exception {
        when(languageService.getLanguageById(1L)).thenReturn(languageDTO);

        mockMvc.perform(get("/api/languages/1").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("English"));

        verify(languageService, times(1)).getLanguageById(1L);
    }

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void createLanguage_WithValidRequest_ReturnsCreated() throws Exception {
        LanguageDTO newLanguage = LanguageDTO.builder().name("French").build();
        LanguageDTO createdLanguage = LanguageDTO.builder().id(2L).name("French").build();

        when(languageService.createLanguage(any(LanguageDTO.class))).thenReturn(createdLanguage);

        mockMvc.perform(post("/api/languages")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newLanguage)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(2L))
                .andExpect(jsonPath("$.name").value("French"));

        verify(languageService, times(1)).createLanguage(any(LanguageDTO.class));
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void createLanguage_WithCustomerRole_ReturnsForbidden() throws Exception {
        LanguageDTO newLanguage = LanguageDTO.builder().name("French").build();

        mockMvc.perform(post("/api/languages")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newLanguage)))
                .andExpect(status().isForbidden());

        verify(languageService, never()).createLanguage(any(LanguageDTO.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateLanguage_ReturnsOk() throws Exception {
        LanguageDTO updated = LanguageDTO.builder().id(1L).name("English (US)").build();

        when(languageService.updateLanguage(eq(1L), any(LanguageDTO.class))).thenReturn(updated);

        mockMvc.perform(put("/api/languages/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(languageDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("English (US)"));

        verify(languageService, times(1)).updateLanguage(eq(1L), any(LanguageDTO.class));
    }

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void deleteLanguage_ReturnsNoContent() throws Exception {
        doNothing().when(languageService).deleteLanguage(1L);

        mockMvc.perform(delete("/api/languages/1").with(csrf()))
                .andExpect(status().isNoContent());

        verify(languageService, times(1)).deleteLanguage(1L);
    }
}
