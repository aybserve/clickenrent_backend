package org.clickenrent.rentalservice.service;

import org.clickenrent.rentalservice.dto.ChargingStationBrandDTO;
import org.clickenrent.rentalservice.entity.ChargingStationBrand;
import org.clickenrent.rentalservice.exception.ResourceNotFoundException;
import org.clickenrent.rentalservice.mapper.ChargingStationBrandMapper;
import org.clickenrent.rentalservice.repository.ChargingStationBrandRepository;
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
class ChargingStationBrandServiceTest {

    @Mock
    private ChargingStationBrandRepository chargingStationBrandRepository;

    @Mock
    private ChargingStationBrandMapper chargingStationBrandMapper;

    @Mock
    private SecurityService securityService;

    @InjectMocks
    private ChargingStationBrandService chargingStationBrandService;

    private ChargingStationBrand testBrand;
    private ChargingStationBrandDTO testBrandDTO;

    @BeforeEach
    void setUp() {
        testBrand = ChargingStationBrand.builder()
        .id(1L)
        .name("Tesla")
        .companyId(1L)
        .build();

        testBrandDTO = ChargingStationBrandDTO.builder()
        .id(1L)
        .name("Tesla")
        .companyId(1L)
        .build();

            }

    @Test
    void getAllBrands_ReturnsAllBrands() {
        Pageable pageable = PageRequest.of(0, 20);
        Page<ChargingStationBrand> brandPage = new PageImpl<>(Collections.singletonList(testBrand));
        when(chargingStationBrandRepository.findAll(pageable)).thenReturn(brandPage);
        when(chargingStationBrandMapper.toDto(testBrand)).thenReturn(testBrandDTO);

        Page<ChargingStationBrandDTO> result = chargingStationBrandService.getAllBrands(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void getBrandsByCompany_Success() {
        when(securityService.isAdmin()).thenReturn(true);
        when(chargingStationBrandRepository.findByCompanyId(1L)).thenReturn(Collections.singletonList(testBrand));
        when(chargingStationBrandMapper.toDto(testBrand)).thenReturn(testBrandDTO);

        List<ChargingStationBrandDTO> result = chargingStationBrandService.getBrandsByCompany(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Tesla", result.get(0).getName());
        verify(chargingStationBrandRepository, times(1)).findByCompanyId(1L);
    }

    @Test
    void getBrandById_Success() {
        when(chargingStationBrandRepository.findById(1L)).thenReturn(Optional.of(testBrand));
        when(chargingStationBrandMapper.toDto(testBrand)).thenReturn(testBrandDTO);

        ChargingStationBrandDTO result = chargingStationBrandService.getBrandById(1L);

        assertNotNull(result);
        assertEquals("Tesla", result.getName());
    }

    @Test
    void getBrandById_NotFound() {
        when(chargingStationBrandRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> chargingStationBrandService.getBrandById(999L));
    }

    @Test
    void createBrand_Success() {
        when(securityService.isAdmin()).thenReturn(true);
        when(chargingStationBrandMapper.toEntity(testBrandDTO)).thenReturn(testBrand);
        when(chargingStationBrandRepository.save(any())).thenReturn(testBrand);
        when(chargingStationBrandMapper.toDto(testBrand)).thenReturn(testBrandDTO);

        ChargingStationBrandDTO result = chargingStationBrandService.createBrand(testBrandDTO);

        assertNotNull(result);
        verify(chargingStationBrandRepository, times(1)).save(any());
    }

    @Test
    void updateBrand_Success() {
        when(securityService.isAdmin()).thenReturn(true);
        when(chargingStationBrandRepository.findById(1L)).thenReturn(Optional.of(testBrand));
        doNothing().when(chargingStationBrandMapper).updateEntityFromDto(testBrandDTO, testBrand);
        when(chargingStationBrandRepository.save(any())).thenReturn(testBrand);
        when(chargingStationBrandMapper.toDto(testBrand)).thenReturn(testBrandDTO);

        ChargingStationBrandDTO result = chargingStationBrandService.updateBrand(1L, testBrandDTO);

        assertNotNull(result);
        verify(chargingStationBrandMapper, times(1)).updateEntityFromDto(testBrandDTO, testBrand);
        verify(chargingStationBrandRepository, times(1)).save(any());
    }

    @Test
    void deleteBrand_Success() {
        when(securityService.isAdmin()).thenReturn(true);
        when(chargingStationBrandRepository.findById(1L)).thenReturn(Optional.of(testBrand));
        doNothing().when(chargingStationBrandRepository).delete(testBrand);

        chargingStationBrandService.deleteBrand(1L);

        verify(chargingStationBrandRepository, times(1)).delete(testBrand);
    }

    @Test
    void deleteBrand_NotFound() {
        when(securityService.isAdmin()).thenReturn(true);
        when(chargingStationBrandRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> chargingStationBrandService.deleteBrand(999L));
    }
}


