package org.clickenrent.rentalservice.service;

import lombok.RequiredArgsConstructor;
import org.clickenrent.rentalservice.dto.PartModelDTO;
import org.clickenrent.rentalservice.entity.PartModel;
import org.clickenrent.rentalservice.exception.ResourceNotFoundException;
import org.clickenrent.rentalservice.exception.UnauthorizedException;
import org.clickenrent.rentalservice.mapper.PartModelMapper;
import org.clickenrent.rentalservice.repository.PartModelRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PartModelService {

    private final PartModelRepository partModelRepository;
    private final PartModelMapper partModelMapper;
    private final SecurityService securityService;

    @Transactional(readOnly = true)
    public Page<PartModelDTO> getAllModels(Pageable pageable) {
        return partModelRepository.findAll(pageable)
                .map(partModelMapper::toDto);
    }

    @Transactional(readOnly = true)
    public PartModelDTO getModelById(Long id) {
        PartModel model = partModelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PartModel", "id", id));
        return partModelMapper.toDto(model);
    }

    @Transactional
    public PartModelDTO createModel(PartModelDTO dto) {
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("Only administrators can create part models");
        }

        PartModel model = partModelMapper.toEntity(dto);
        model = partModelRepository.save(model);
        return partModelMapper.toDto(model);
    }

    @Transactional
    public PartModelDTO updateModel(Long id, PartModelDTO dto) {
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("Only administrators can update part models");
        }

        PartModel model = partModelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PartModel", "id", id));

        partModelMapper.updateEntityFromDto(dto, model);
        model = partModelRepository.save(model);
        return partModelMapper.toDto(model);
    }

    @Transactional
    public void deleteModel(Long id) {
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("Only administrators can delete part models");
        }

        PartModel model = partModelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PartModel", "id", id));
        partModelRepository.delete(model);
    }
}
