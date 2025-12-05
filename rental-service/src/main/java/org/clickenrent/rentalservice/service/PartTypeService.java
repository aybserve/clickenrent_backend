package org.clickenrent.rentalservice.service;

import lombok.RequiredArgsConstructor;
import org.clickenrent.rentalservice.dto.PartTypeDTO;
import org.clickenrent.rentalservice.entity.PartType;
import org.clickenrent.rentalservice.exception.ResourceNotFoundException;
import org.clickenrent.rentalservice.mapper.PartTypeMapper;
import org.clickenrent.rentalservice.repository.PartTypeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PartTypeService {

    private final PartTypeRepository partTypeRepository;
    private final PartTypeMapper partTypeMapper;

    @Transactional(readOnly = true)
    public List<PartTypeDTO> getAllTypes() {
        return partTypeRepository.findAll().stream()
                .map(partTypeMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public PartTypeDTO getTypeById(Long id) {
        PartType type = partTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PartType", "id", id));
        return partTypeMapper.toDto(type);
    }
}
