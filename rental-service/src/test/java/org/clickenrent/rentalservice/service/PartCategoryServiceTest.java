package org.clickenrent.rentalservice.service;

import org.clickenrent.rentalservice.dto.PartCategoryDTO;
import org.clickenrent.rentalservice.entity.PartCategory;
import org.clickenrent.rentalservice.exception.ResourceNotFoundException;
import org.clickenrent.rentalservice.mapper.PartCategoryMapper;
import org.clickenrent.rentalservice.repository.PartCategoryRepository;
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
class PartCategoryServiceTest {

    @Mock
    private PartCategoryRepository partCategoryRepository;

    @Mock
    private PartCategoryMapper partCategoryMapper;

    @Mock
    private SecurityService securityService;

    @InjectMocks
    private PartCategoryService partCategoryService;

    private PartCategory testCategory;
    private PartCategoryDTO testCategoryDTO;

    @BeforeEach
    void setUp() {
        testCategory = PartCategory.builder()
        .id(1L)
        .externalId("PC001")
        .name("Battery")
        .build();

        testCategoryDTO = PartCategoryDTO.builder()
        .id(1L)
        .externalId("PC001")
        .name("Battery")
        .build();
    }

    @Test
    void getAllCategories_ReturnsAllCategories() {
        Pageable pageable = PageRequest.of(0, 20);
        Page<PartCategory> categoryPage = new PageImpl<>(Collections.singletonList(testCategory));
        when(partCategoryRepository.findAll(pageable)).thenReturn(categoryPage);
        when(partCategoryMapper.toDto(testCategory)).thenReturn(testCategoryDTO);

        Page<PartCategoryDTO> result = partCategoryService.getAllCategories(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void getCategoryById_Success() {
        when(partCategoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
        when(partCategoryMapper.toDto(testCategory)).thenReturn(testCategoryDTO);

        PartCategoryDTO result = partCategoryService.getCategoryById(1L);

        assertNotNull(result);
        assertEquals("Battery", result.getName());
    }

    @Test
    void getCategoryById_NotFound() {
        when(partCategoryRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> partCategoryService.getCategoryById(999L));
    }

    @Test
    void createCategory_Success() {
        when(securityService.isAdmin()).thenReturn(true);
        when(partCategoryMapper.toEntity(testCategoryDTO)).thenReturn(testCategory);
        when(partCategoryRepository.save(any())).thenReturn(testCategory);
        when(partCategoryMapper.toDto(testCategory)).thenReturn(testCategoryDTO);

        PartCategoryDTO result = partCategoryService.createCategory(testCategoryDTO);

        assertNotNull(result);
    }

    @Test
    void updateCategory_Success() {
        when(securityService.isAdmin()).thenReturn(true);
        when(partCategoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
        doNothing().when(partCategoryMapper).updateEntityFromDto(testCategoryDTO, testCategory);
        when(partCategoryRepository.save(any())).thenReturn(testCategory);
        when(partCategoryMapper.toDto(testCategory)).thenReturn(testCategoryDTO);

        PartCategoryDTO result = partCategoryService.updateCategory(1L, testCategoryDTO);

        assertNotNull(result);
    }

    @Test
    void deleteCategory_Success() {
        when(securityService.isAdmin()).thenReturn(true);
        when(partCategoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
        doNothing().when(partCategoryRepository).delete(testCategory);

        partCategoryService.deleteCategory(1L);

        verify(partCategoryRepository, times(1)).delete(testCategory);
    }
}








