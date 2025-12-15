package org.clickenrent.rentalservice.service;

import org.clickenrent.rentalservice.dto.BikeBrandDTO;
import org.clickenrent.rentalservice.entity.BikeBrand;
import org.clickenrent.rentalservice.exception.ResourceNotFoundException;
import org.clickenrent.rentalservice.mapper.BikeBrandMapper;
import org.clickenrent.rentalservice.repository.BikeBrandRepository;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BikeBrandServiceTest {

    @Mock
    private BikeBrandRepository bikeBrandRepository;

    @Mock
    private BikeBrandMapper bikeBrandMapper;

    @InjectMocks
    private BikeBrandService bikeBrandService;

    private BikeBrand testBrand;
    private BikeBrandDTO testBrandDTO;

    @BeforeEach
    void setUp() {
        testBrand = BikeBrand.builder()
                .id(1L)
                .externalId("BB001")
                .name("VanMoof")
                .build();

        testBrandDTO = BikeBrandDTO.builder()
                .id(1L)
                .externalId("BB001")
                .name("VanMoof")
                .companyId(1L)
                .build();
    }

    @Test
    void getAllBikeBrands_ReturnsAllBrands() {
        Pageable pageable = PageRequest.of(0, 20);
        Page<BikeBrand> brandPage = new PageImpl<>(Collections.singletonList(testBrand));
        when(bikeBrandRepository.findAll(pageable)).thenReturn(brandPage);
        when(bikeBrandMapper.toDto(testBrand)).thenReturn(testBrandDTO);

        Page<BikeBrandDTO> result = bikeBrandService.getAllBikeBrands(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(bikeBrandRepository, times(1)).findAll(pageable);
    }

    @Test
    void getBikeBrandById_Success() {
        when(bikeBrandRepository.findById(1L)).thenReturn(Optional.of(testBrand));
        when(bikeBrandMapper.toDto(testBrand)).thenReturn(testBrandDTO);

        BikeBrandDTO result = bikeBrandService.getBikeBrandById(1L);

        assertNotNull(result);
        assertEquals("VanMoof", result.getName());
        verify(bikeBrandRepository, times(1)).findById(1L);
    }

    @Test
    void getBikeBrandById_NotFound() {
        when(bikeBrandRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bikeBrandService.getBikeBrandById(999L));
    }

    @Test
    void createBikeBrand_Success() {
        when(bikeBrandMapper.toEntity(testBrandDTO)).thenReturn(testBrand);
        when(bikeBrandRepository.save(any())).thenReturn(testBrand);
        when(bikeBrandMapper.toDto(testBrand)).thenReturn(testBrandDTO);

        BikeBrandDTO result = bikeBrandService.createBikeBrand(testBrandDTO);

        assertNotNull(result);
        verify(bikeBrandRepository, times(1)).save(any());
    }

    @Test
    void updateBikeBrand_Success() {
        when(bikeBrandRepository.findById(1L)).thenReturn(Optional.of(testBrand));
        when(bikeBrandRepository.save(any())).thenReturn(testBrand);
        when(bikeBrandMapper.toDto(testBrand)).thenReturn(testBrandDTO);

        BikeBrandDTO result = bikeBrandService.updateBikeBrand(1L, testBrandDTO);

        assertNotNull(result);
        verify(bikeBrandRepository, times(1)).save(any());
    }

    @Test
    void deleteBikeBrand_Success() {
        when(bikeBrandRepository.findById(1L)).thenReturn(Optional.of(testBrand));
        doNothing().when(bikeBrandRepository).delete(testBrand);

        bikeBrandService.deleteBikeBrand(1L);

        verify(bikeBrandRepository, times(1)).delete(testBrand);
    }
}
