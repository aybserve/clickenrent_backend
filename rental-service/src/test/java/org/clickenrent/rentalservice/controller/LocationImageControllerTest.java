package org.clickenrent.rentalservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.clickenrent.rentalservice.dto.LocationImageDTO;
import org.clickenrent.rentalservice.service.LocationImageService;
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

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LocationImageController.class)
@AutoConfigureMockMvc
class LocationImageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private LocationImageService locationImageService;

    private LocationImageDTO imageDTO;

    @BeforeEach
    void setUp() {
        imageDTO = LocationImageDTO.builder()
                .id(1L)
                .externalId("IMG001")
                .locationId(1L)
                .imageUrl("https://example.com/location.jpg")
                .sortOrder(1)
                .isThumbnail(true)
                .build();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getImagesByLocation_ReturnsOk() throws Exception {
        when(locationImageService.getImagesByLocation(1L)).thenReturn(Arrays.asList(imageDTO));

        mockMvc.perform(get("/api/location-images/by-location/1").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].imageUrl").value("https://example.com/location.jpg"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getImageById_ReturnsOk() throws Exception {
        when(locationImageService.getImageById(1L)).thenReturn(imageDTO);

        mockMvc.perform(get("/api/location-images/1").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.imageUrl").value("https://example.com/location.jpg"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createImage_ReturnsCreated() throws Exception {
        when(locationImageService.createImage(any())).thenReturn(imageDTO);

        mockMvc.perform(post("/api/location-images")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(imageDTO)))
                .andExpect(status().isCreated());
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    void updateImage_ReturnsOk() throws Exception {
        when(locationImageService.updateImage(eq(1L), any())).thenReturn(imageDTO);

        mockMvc.perform(put("/api/location-images/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(imageDTO)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteImage_ReturnsNoContent() throws Exception {
        doNothing().when(locationImageService).deleteImage(1L);

        mockMvc.perform(delete("/api/location-images/1").with(csrf()))
                .andExpect(status().isNoContent());
    }
}
