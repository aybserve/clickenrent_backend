package org.clickenrent.rentalservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.clickenrent.rentalservice.dto.UserLocationDTO;
import org.clickenrent.rentalservice.service.UserLocationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserLocationController.class)
@AutoConfigureMockMvc
class UserLocationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserLocationService userLocationService;

    private UserLocationDTO userLocationDTO;

    @BeforeEach
    void setUp() {
        userLocationDTO = UserLocationDTO.builder()
                .id(1L)
                .userId(1L)
                .locationId(1L)
                .locationRoleId(1L)
                .build();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getUserLocationsByUser_ReturnsOk() throws Exception {
        List<UserLocationDTO> locations = Collections.singletonList(userLocationDTO);
        when(userLocationService.getUserLocationsByUser(1L)).thenReturn(locations);

        mockMvc.perform(get("/api/user-locations/by-user/1").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userId").value(1L));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getUserLocationsByLocation_ReturnsOk() throws Exception {
        List<UserLocationDTO> users = Collections.singletonList(userLocationDTO);
        when(userLocationService.getUserLocationsByLocation(1L)).thenReturn(users);

        mockMvc.perform(get("/api/user-locations/by-location/1").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].locationId").value(1L));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void assignUserToLocation_ReturnsCreated() throws Exception {
        when(userLocationService.assignUserToLocation(any())).thenReturn(userLocationDTO);

        mockMvc.perform(post("/api/user-locations")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userLocationDTO)))
                .andExpect(status().isCreated());
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    void removeUserFromLocation_ReturnsNoContent() throws Exception {
        doNothing().when(userLocationService).removeUserFromLocation(1L);

        mockMvc.perform(delete("/api/user-locations/1").with(csrf()))
                .andExpect(status().isNoContent());
    }
}




