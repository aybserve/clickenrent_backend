package org.clickenrent.rentalservice.service;

import org.clickenrent.rentalservice.dto.B2BSaleDTO;
import org.clickenrent.rentalservice.entity.B2BSale;
import org.clickenrent.rentalservice.exception.ResourceNotFoundException;
import org.clickenrent.rentalservice.mapper.B2BSaleMapper;
import org.clickenrent.rentalservice.repository.B2BSaleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class B2BSaleServiceTest {

    @Mock
    private B2BSaleRepository b2bSaleRepository;

    @Mock
    private B2BSaleMapper b2bSaleMapper;

    @InjectMocks
    private B2BSaleService b2bSaleService;

    private B2BSale testSale;
    private B2BSaleDTO testSaleDTO;

    @BeforeEach
    void setUp() {
        testSale = B2BSale.builder()
                .id(1L)
                .externalId("B2BS001")
                .dateTime(LocalDateTime.now())
                .build();

        testSaleDTO = B2BSaleDTO.builder()
                .id(1L)
                .externalId("B2BS001")
                .locationId(1L)
                .b2bSaleStatusId(2L)
                .dateTime(LocalDateTime.now())
                .build();
    }

    @Test
    void getAllSales_ReturnsAllSales() {
        Pageable pageable = PageRequest.of(0, 20);
        Page<B2BSale> salePage = new PageImpl<>(Collections.singletonList(testSale));
        when(b2bSaleRepository.findAll(pageable)).thenReturn(salePage);
        when(b2bSaleMapper.toDto(testSale)).thenReturn(testSaleDTO);

        Page<B2BSaleDTO> result = b2bSaleService.getAllSales(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void getSaleById_Success() {
        when(b2bSaleRepository.findById(1L)).thenReturn(Optional.of(testSale));
        when(b2bSaleMapper.toDto(testSale)).thenReturn(testSaleDTO);

        B2BSaleDTO result = b2bSaleService.getSaleById(1L);

        assertNotNull(result);
        assertEquals("B2BS001", result.getExternalId());
        verify(b2bSaleRepository, times(1)).findById(1L);
    }

    @Test
    void getSaleById_NotFound() {
        when(b2bSaleRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> b2bSaleService.getSaleById(999L));
    }

    @Test
    void createSale_Success() {
        when(b2bSaleMapper.toEntity(testSaleDTO)).thenReturn(testSale);
        when(b2bSaleRepository.save(any())).thenReturn(testSale);
        when(b2bSaleMapper.toDto(testSale)).thenReturn(testSaleDTO);

        B2BSaleDTO result = b2bSaleService.createSale(testSaleDTO);

        assertNotNull(result);
    }

    @Test
    void updateSale_Success() {
        when(b2bSaleRepository.findById(1L)).thenReturn(Optional.of(testSale));
        when(b2bSaleRepository.save(any())).thenReturn(testSale);
        when(b2bSaleMapper.toDto(testSale)).thenReturn(testSaleDTO);

        B2BSaleDTO result = b2bSaleService.updateSale(1L, testSaleDTO);

        assertNotNull(result);
    }

    @Test
    void deleteSale_Success() {
        when(b2bSaleRepository.findById(1L)).thenReturn(Optional.of(testSale));
        doNothing().when(b2bSaleRepository).delete(testSale);

        b2bSaleService.deleteSale(1L);

        verify(b2bSaleRepository, times(1)).delete(testSale);
    }
}
