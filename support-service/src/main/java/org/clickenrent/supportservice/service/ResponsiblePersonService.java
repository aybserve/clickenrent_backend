package org.clickenrent.supportservice.service;

import lombok.RequiredArgsConstructor;
import org.clickenrent.supportservice.dto.ResponsiblePersonDTO;
import org.clickenrent.supportservice.entity.ResponsiblePerson;
import org.clickenrent.supportservice.exception.ResourceNotFoundException;
import org.clickenrent.supportservice.exception.UnauthorizedException;
import org.clickenrent.supportservice.mapper.ResponsiblePersonMapper;
import org.clickenrent.supportservice.repository.ResponsiblePersonRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for managing ResponsiblePerson entities.
 */
@Service
@RequiredArgsConstructor
public class ResponsiblePersonService {

    private final ResponsiblePersonRepository responsiblePersonRepository;
    private final ResponsiblePersonMapper responsiblePersonMapper;
    private final SecurityService securityService;

    @Transactional(readOnly = true)
    public List<ResponsiblePersonDTO> getAll() {
        return responsiblePersonRepository.findAll().stream()
                .map(responsiblePersonMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ResponsiblePersonDTO getById(Long id) {
        ResponsiblePerson entity = responsiblePersonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ResponsiblePerson", "id", id));
        return responsiblePersonMapper.toDto(entity);
    }

    @Transactional
    public ResponsiblePersonDTO create(ResponsiblePersonDTO dto) {
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("Only administrators can create responsible persons");
        }

        ResponsiblePerson entity = responsiblePersonMapper.toEntity(dto);
        entity = responsiblePersonRepository.save(entity);
        return responsiblePersonMapper.toDto(entity);
    }

    @Transactional
    public ResponsiblePersonDTO update(Long id, ResponsiblePersonDTO dto) {
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("Only administrators can update responsible persons");
        }

        ResponsiblePerson entity = responsiblePersonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ResponsiblePerson", "id", id));

        responsiblePersonMapper.updateEntityFromDto(dto, entity);
        entity = responsiblePersonRepository.save(entity);
        return responsiblePersonMapper.toDto(entity);
    }

    @Transactional
    public void delete(Long id) {
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("Only administrators can delete responsible persons");
        }

        ResponsiblePerson entity = responsiblePersonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ResponsiblePerson", "id", id));
        responsiblePersonRepository.delete(entity);
    }
}

