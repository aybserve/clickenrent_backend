package org.clickenrent.rentalservice.service;

import org.clickenrent.rentalservice.dto.PartBrandDTO;
import org.clickenrent.rentalservice.entity.PartBrand;
import org.clickenrent.rentalservice.exception.ResourceNotFoundException;
import org.clickenrent.rentalservice.mapper.PartBrandMapper;
import org.clickenrent.rentalservice.repository.PartBrandRepository;
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
class PartBrandServiceTest {

    @Mock
    private PartBrandRepository partBrandRepository;

    @Mock
    private PartBrandMapper partBrandMapper;

    @Mock
    private SecurityService securityService;

    @InjectMocks
    private PartBrandService partBrandService;

    private PartBrand testBrand;
    private PartBrandDTO testBrandDTO;

    @BeforeEach
    void setUp() {
        testBrand = PartBrand.builder()
                .id(1L)
                .name("Samsung")
                .companyId(1L)
                .build();

        testBrandDTO = PartBrandDTO.builder()
                .id(1L)
                .name("Samsung")
                .companyId(1L)
                .build();

        when(securityService.isAdmin()).thenReturn(true);
        when(securityService.hasAccessToCompany(anyLong())).thenReturn(true);
    }

    @Test
    void getAllBrands_ReturnsAllBrands() {
        Pageable pageable = PageRequest.of(0, 20);
        Page<PartBrand> brandPage = new PageImpl<>(Collections.singletonList(testBrand));
        when(partBrandRepository.findAll(pageable)).thenReturn(brandPage);
        when(partBrandMapper.toDto(testBrand)).thenReturn(testBrandDTO);

        Page<PartBrandDTO> result = partBrandService.getAllBrands(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(partBrandRepository, times(1)).findAll(pageable);
    }

    @Test
    void getBrandsByCompany_Success() {
        when(partBrandRepository.findByCompanyId(1L)).thenReturn(Collections.singletonList(testBrand));
        when(partBrandMapper.toDto(testBrand)).thenReturn(testBrandDTO);

        List<PartBrandDTO> result = partBrandService.getBrandsByCompany(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Samsung", result.get(0).getName());
        verify(partBrandRepository, times(1)).findByCompanyId(1L);
    }

    @Test
    void getBrandById_Success() {
        when(partBrandRepository.findById(1L)).thenReturn(Optional.of(testBrand));
        when(partBrandMapper.toDto(testBrand)).thenReturn(testBrandDTO);

        PartBrandDTO result = partBrandService.getBrandById(1L);

        assertNotNull(result);
        assertEquals("Samsung", result.getName());
        verify(partBrandRepository, times(1)).findById(1L);
    }

    @Test
    void getBrandById_NotFound() {
        when(partBrandRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> partBrandService.getBrandById(999L));
    }

    @Test
    void createBrand_Success() {
        when(partBrandMapper.toEntity(testBrandDTO)).thenReturn(testBrand);
        when(partBrandRepository.save(any())).thenReturn(testBrand);
        when(partBrandMapper.toDto(testBrand)).thenReturn(testBrandDTO);

        PartBrandDTO result = partBrandService.createBrand(testBrandDTO);

        assertNotNull(result);
        verify(partBrandRepository, times(1)).save(any());
    }

    @Test
    void updateBrand_Success() {
        when(partBrandRepository.findById(1L)).thenReturn(Optional.of(testBrand));
        doNothing().when(partBrandMapper).updateEntityFromDto(testBrandDTO, testBrand);
        when(partBrandRepository.save(any())).thenReturn(testBrand);
        when(partBrandMapper.toDto(testBrand)).thenReturn(testBrandDTO);

        PartBrandDTO result = partBrandService.updateBrand(1L, testBrandDTO);

        assertNotNull(result);
        verify(partBrandMapper, times(1)).updateEntityFromDto(testBrandDTO, testBrand);
        verify(partBrandRepository, times(1)).save(any());
    }

    @Test
    void deleteBrand_Success() {
        when(partBrandRepository.findById(1L)).thenReturn(Optional.of(testBrand));
        doNothing().when(partBrandRepository).delete(testBrand);

        partBrandService.deleteBrand(1L);

        verify(partBrandRepository, times(1)).delete(testBrand);
    }

    @Test
    void deleteBrand_NotFound() {
        when(partBrandRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> partBrandService.deleteBrand(999L));
    }
}
