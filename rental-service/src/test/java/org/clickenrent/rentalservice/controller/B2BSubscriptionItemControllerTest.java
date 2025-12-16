package org.clickenrent.rentalservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.clickenrent.rentalservice.dto.B2BSubscriptionItemDTO;
import org.clickenrent.rentalservice.service.B2BSubscriptionItemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(B2BSubscriptionItemController.class)
@AutoConfigureMockMvc
class B2BSubscriptionItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private B2BSubscriptionItemService b2bSubscriptionItemService;

    private B2BSubscriptionItemDTO itemDTO;

    @BeforeEach
    void setUp() {
        itemDTO = B2BSubscriptionItemDTO.builder()
                .id(1L)
                .externalId("BSUBI001")
                .b2bSubscriptionId(1L)
                .productId(1L)
                .startDateTime(LocalDateTime.now())
                .endDateTime(LocalDateTime.now().plusMonths(1))
                .price(new BigDecimal("100.00"))
                .totalPrice(new BigDecimal("3000.00"))
                .build();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getItemsBySubscription_ReturnsOk() throws Exception {
        List<B2BSubscriptionItemDTO> items = Collections.singletonList(itemDTO);
        when(b2bSubscriptionItemService.getItemsBySubscription(1L)).thenReturn(items);

        mockMvc.perform(get("/api/b2b-subscription-items/by-subscription/1").with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getItemById_ReturnsOk() throws Exception {
        when(b2bSubscriptionItemService.getItemById(1L)).thenReturn(itemDTO);

        mockMvc.perform(get("/api/b2b-subscription-items/1").with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createItem_ReturnsCreated() throws Exception {
        when(b2bSubscriptionItemService.createItem(any())).thenReturn(itemDTO);

        mockMvc.perform(post("/api/b2b-subscription-items")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDTO)))
                .andExpect(status().isCreated());
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    void updateItem_ReturnsOk() throws Exception {
        when(b2bSubscriptionItemService.updateItem(eq(1L), any())).thenReturn(itemDTO);

        mockMvc.perform(put("/api/b2b-subscription-items/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDTO)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteItem_ReturnsNoContent() throws Exception {
        doNothing().when(b2bSubscriptionItemService).deleteItem(1L);

        mockMvc.perform(delete("/api/b2b-subscription-items/1").with(csrf()))
                .andExpect(status().isNoContent());
    }
}
