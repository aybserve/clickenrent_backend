package org.clickenrent.rentalservice.service;

import org.clickenrent.rentalservice.dto.B2BSaleOrderProductModelDTO;
import org.clickenrent.rentalservice.entity.B2BSaleOrderProductModel;
import org.clickenrent.rentalservice.exception.ResourceNotFoundException;
import org.clickenrent.rentalservice.mapper.B2BSaleOrderProductModelMapper;
import org.clickenrent.rentalservice.repository.B2BSaleOrderProductModelRepository;
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

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class B2BSaleOrderProductModelServiceTest {

    @Mock
    private B2BSaleOrderProductModelRepository b2bSaleOrderProductModelRepository;

    @Mock
    private B2BSaleOrderProductModelMapper b2bSaleOrderProductModelMapper;

    @Mock
    private SecurityService securityService;


    @InjectMocks
    private B2BSaleOrderProductModelService b2bSaleOrderProductModelService;

    private B2BSaleOrderProductModel testProductModel;
    private B2BSaleOrderProductModelDTO testProductModelDTO;

    @BeforeEach
    void setUp() {
        testProductModel = B2BSaleOrderProductModel.builder()
        .id(1L)
        .build();

        testProductModelDTO = B2BSaleOrderProductModelDTO.builder()
        .id(1L)
        .b2bSaleOrderId(1L)
        .build();
    }

    @Test
    void getAllB2BSaleOrderProductModels_ReturnsAll() {
        when(securityService.isAdmin()).thenReturn(true);
        Pageable pageable = PageRequest.of(0, 20);
        Page<B2BSaleOrderProductModel> productModelPage = new PageImpl<>(Collections.singletonList(testProductModel));
        when(b2bSaleOrderProductModelRepository.findAll(pageable)).thenReturn(productModelPage);
        when(b2bSaleOrderProductModelMapper.toDto(testProductModel)).thenReturn(testProductModelDTO);

        Page<B2BSaleOrderProductModelDTO> result = b2bSaleOrderProductModelService.getAllProductModels(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(b2bSaleOrderProductModelRepository, times(1)).findAll(pageable);
    }

    @Test
    void getProductModelsByOrderId_Success() {
        when(b2bSaleOrderProductModelRepository.findByB2bSaleOrderId(1L))
        .thenReturn(Collections.singletonList(testProductModel));
        when(b2bSaleOrderProductModelMapper.toDto(testProductModel)).thenReturn(testProductModelDTO);

        List<B2BSaleOrderProductModelDTO> result = b2bSaleOrderProductModelService.getProductModelsByOrderId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(b2bSaleOrderProductModelRepository, times(1)).findByB2bSaleOrderId(1L);
    }

    @Test
    void getProductModelById_Success() {
        when(securityService.isAdmin()).thenReturn(true);
        when(b2bSaleOrderProductModelRepository.findById(1L)).thenReturn(Optional.of(testProductModel));
        when(b2bSaleOrderProductModelMapper.toDto(testProductModel)).thenReturn(testProductModelDTO);

        B2BSaleOrderProductModelDTO result = b2bSaleOrderProductModelService.getProductModelById(1L);

        assertNotNull(result);
        verify(b2bSaleOrderProductModelRepository, times(1)).findById(1L);
    }

    @Test
    void getProductModelById_NotFound() {
        when(b2bSaleOrderProductModelRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> b2bSaleOrderProductModelService.getProductModelById(999L));
    }

    @Test
    void createProductModel_Success() {
        when(securityService.isAdmin()).thenReturn(true);
        when(b2bSaleOrderProductModelMapper.toEntity(testProductModelDTO)).thenReturn(testProductModel);
        when(b2bSaleOrderProductModelRepository.save(any())).thenReturn(testProductModel);
        when(b2bSaleOrderProductModelMapper.toDto(testProductModel)).thenReturn(testProductModelDTO);

        B2BSaleOrderProductModelDTO result = b2bSaleOrderProductModelService.createProductModel(testProductModelDTO);

        assertNotNull(result);
        verify(b2bSaleOrderProductModelRepository, times(1)).save(any());
    }

    @Test
    void updateProductModel_Success() {
        when(securityService.isAdmin()).thenReturn(true);
        when(b2bSaleOrderProductModelRepository.findById(1L)).thenReturn(Optional.of(testProductModel));
        doNothing().when(b2bSaleOrderProductModelMapper).updateEntityFromDto(testProductModelDTO, testProductModel);
        when(b2bSaleOrderProductModelRepository.save(any())).thenReturn(testProductModel);
        when(b2bSaleOrderProductModelMapper.toDto(testProductModel)).thenReturn(testProductModelDTO);

        B2BSaleOrderProductModelDTO result = b2bSaleOrderProductModelService.updateProductModel(1L, testProductModelDTO);

        assertNotNull(result);
        verify(b2bSaleOrderProductModelMapper, times(1)).updateEntityFromDto(testProductModelDTO, testProductModel);
        verify(b2bSaleOrderProductModelRepository, times(1)).save(any());
    }

    @Test
    void deleteProductModel_Success() {
        when(securityService.isAdmin()).thenReturn(true);
        when(b2bSaleOrderProductModelRepository.findById(1L)).thenReturn(Optional.of(testProductModel));
        doNothing().when(b2bSaleOrderProductModelRepository).delete(testProductModel);

        b2bSaleOrderProductModelService.deleteProductModel(1L);

        verify(b2bSaleOrderProductModelRepository, times(1)).delete(testProductModel);
    }

    @Test
    void deleteProductModel_NotFound() {
        when(securityService.isAdmin()).thenReturn(true);
        when(b2bSaleOrderProductModelRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> b2bSaleOrderProductModelService.deleteProductModel(999L));
    }
}







