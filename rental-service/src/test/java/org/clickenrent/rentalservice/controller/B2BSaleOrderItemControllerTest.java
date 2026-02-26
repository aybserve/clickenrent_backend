package org.clickenrent.rentalservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.clickenrent.rentalservice.dto.B2BSaleOrderItemDTO;
import org.clickenrent.rentalservice.service.B2BSaleOrderItemService;
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
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(B2BSaleOrderItemController.class)
@AutoConfigureMockMvc
class B2BSaleOrderItemControllerTest extends BaseWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private B2BSaleOrderItemService b2bSaleOrderItemService;

    private B2BSaleOrderItemDTO itemDTO;

    @BeforeEach
    void setUp() {
        itemDTO = B2BSaleOrderItemDTO.builder()
                .id(1L)
                .b2bSaleOrderId(1L)
                .build();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllItems_ReturnsOk() throws Exception {
        Page<B2BSaleOrderItemDTO> page = new PageImpl<>(Collections.singletonList(itemDTO));
        when(b2bSaleOrderItemService.getAllItems(any())).thenReturn(page);

        mockMvc.perform(get("/api/b2b-sale-order-items").with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getItemsByOrderId_ReturnsOk() throws Exception {
        List<B2BSaleOrderItemDTO> items = Collections.singletonList(itemDTO);
        when(b2bSaleOrderItemService.getItemsByOrderId(1L)).thenReturn(items);

        mockMvc.perform(get("/api/b2b-sale-order-items/by-order/1").with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getItemById_ReturnsOk() throws Exception {
        when(b2bSaleOrderItemService.getItemById(1L)).thenReturn(itemDTO);

        mockMvc.perform(get("/api/b2b-sale-order-items/1").with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createItem_ReturnsCreated() throws Exception {
        when(b2bSaleOrderItemService.createItem(any())).thenReturn(itemDTO);

        mockMvc.perform(post("/api/b2b-sale-order-items")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDTO)))
                .andExpect(status().isCreated());
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteItem_ReturnsNoContent() throws Exception {
        doNothing().when(b2bSaleOrderItemService).deleteItem(1L);

        mockMvc.perform(delete("/api/b2b-sale-order-items/1").with(csrf()))
                .andExpect(status().isNoContent());
    }
}

