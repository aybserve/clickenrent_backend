package org.clickenrent.rentalservice.service;

import lombok.RequiredArgsConstructor;
import org.clickenrent.rentalservice.dto.PartCategoryDTO;
import org.clickenrent.rentalservice.entity.PartCategory;
import org.clickenrent.rentalservice.exception.ResourceNotFoundException;
import org.clickenrent.rentalservice.exception.UnauthorizedException;
import org.clickenrent.rentalservice.mapper.PartCategoryMapper;
import org.clickenrent.rentalservice.repository.PartCategoryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PartCategoryService {

    private final PartCategoryRepository partCategoryRepository;
    private final PartCategoryMapper partCategoryMapper;
    private final SecurityService securityService;

    @Transactional(readOnly = true)
    public Page<PartCategoryDTO> getAllCategories(Pageable pageable) {
        return partCategoryRepository.findAll(pageable)
                .map(partCategoryMapper::toDto);
    }

    @Transactional(readOnly = true)
    public PartCategoryDTO getCategoryById(Long id) {
        PartCategory category = partCategoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PartCategory", "id", id));
        return partCategoryMapper.toDto(category);
    }

    @Transactional
    public PartCategoryDTO createCategory(PartCategoryDTO dto) {
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("Only administrators can create part categories");
        }

        PartCategory category = partCategoryMapper.toEntity(dto);
        category = partCategoryRepository.save(category);
        return partCategoryMapper.toDto(category);
    }

    @Transactional
    public PartCategoryDTO updateCategory(Long id, PartCategoryDTO dto) {
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("Only administrators can update part categories");
        }

        PartCategory category = partCategoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PartCategory", "id", id));

        partCategoryMapper.updateEntityFromDto(dto, category);
        category = partCategoryRepository.save(category);
        return partCategoryMapper.toDto(category);
    }

    @Transactional
    public void deleteCategory(Long id) {
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("Only administrators can delete part categories");
        }

        PartCategory category = partCategoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PartCategory", "id", id));
        partCategoryRepository.delete(category);
    }
}


