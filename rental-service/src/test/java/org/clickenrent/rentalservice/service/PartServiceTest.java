package org.clickenrent.rentalservice.service;

import org.clickenrent.rentalservice.dto.PartDTO;
import org.clickenrent.rentalservice.entity.Part;
import org.clickenrent.rentalservice.exception.ResourceNotFoundException;
import org.clickenrent.rentalservice.exception.UnauthorizedException;
import org.clickenrent.rentalservice.mapper.PartMapper;
import org.clickenrent.rentalservice.repository.PartRepository;
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
class PartServiceTest {

    @Mock
    private PartRepository partRepository;

    @Mock
    private PartMapper partMapper;

    @Mock
    private SecurityService securityService;


    @InjectMocks
    private PartService partService;

    private Part testPart;
    private PartDTO testPartDTO;

    @BeforeEach
    void setUp() {
        testPart = Part.builder()
        .id(1L)
        .externalId("PART001")
        .name("Test Part")
        .build();

        testPartDTO = PartDTO.builder()
        .id(1L)
        .externalId("PART001")
        .name("Test Part")
        .partCategoryId(1L)
        .partBrandId(1L)
        .hubId(1L)
        .quantity(10)
        .build();
    }

    @Test
    void getAllParts_WithAdminRole_ReturnsAllParts() {
        Pageable pageable = PageRequest.of(0, 20);
        Page<Part> partPage = new PageImpl<>(Collections.singletonList(testPart));
        when(partRepository.findAll(pageable)).thenReturn(partPage);
        when(partMapper.toDto(testPart)).thenReturn(testPartDTO);

        Page<PartDTO> result = partService.getAllParts(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(partRepository, times(1)).findAll(pageable);
    }

    @Test
    void getPartById_Success() {
        when(partRepository.findById(1L)).thenReturn(Optional.of(testPart));
        when(partMapper.toDto(testPart)).thenReturn(testPartDTO);

        PartDTO result = partService.getPartById(1L);

        assertNotNull(result);
        assertEquals("PART001", result.getExternalId());
        verify(partRepository, times(1)).findById(1L);
    }

    @Test
    void getPartById_NotFound() {
        when(partRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> partService.getPartById(999L));
    }

    @Test
    void createPart_WithAdminRole_Success() {
        when(securityService.isAdmin()).thenReturn(true);
        when(partMapper.toEntity(testPartDTO)).thenReturn(testPart);
        when(partRepository.save(any())).thenReturn(testPart);
        when(partMapper.toDto(testPart)).thenReturn(testPartDTO);

        PartDTO result = partService.createPart(testPartDTO);

        assertNotNull(result);
        verify(partRepository, times(1)).save(any());
    }

    @Test
    void createPart_WithoutAdminRole_ThrowsUnauthorizedException() {
        assertThrows(UnauthorizedException.class, () -> partService.createPart(testPartDTO));
    }

    @Test
    void updatePart_Success() {
        when(securityService.isAdmin()).thenReturn(true);
        when(partRepository.findById(1L)).thenReturn(Optional.of(testPart));
        when(partRepository.save(any())).thenReturn(testPart);
        when(partMapper.toDto(testPart)).thenReturn(testPartDTO);

        PartDTO result = partService.updatePart(1L, testPartDTO);

        assertNotNull(result);
        verify(partRepository, times(1)).save(any());
    }

    @Test
    void deletePart_Success() {
        when(securityService.isAdmin()).thenReturn(true);
        when(partRepository.findById(1L)).thenReturn(Optional.of(testPart));
        doNothing().when(partRepository).delete(testPart);

        partService.deletePart(1L);

        verify(partRepository, times(1)).delete(testPart);
    }
}
