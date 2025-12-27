package org.clickenrent.rentalservice.service;

import lombok.RequiredArgsConstructor;
import org.clickenrent.rentalservice.dto.PartDTO;
import org.clickenrent.rentalservice.entity.Part;
import org.clickenrent.rentalservice.exception.ResourceNotFoundException;
import org.clickenrent.rentalservice.exception.UnauthorizedException;
import org.clickenrent.rentalservice.mapper.PartMapper;
import org.clickenrent.rentalservice.repository.PartRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PartService {

    private final PartRepository partRepository;
    private final PartMapper partMapper;
    private final SecurityService securityService;

    @Transactional(readOnly = true)
    public Page<PartDTO> getAllParts(Pageable pageable) {
        return partRepository.findAll(pageable)
                .map(partMapper::toDto);
    }

    @Transactional(readOnly = true)
    public PartDTO getPartById(Long id) {
        Part part = partRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Part", "id", id));
        return partMapper.toDto(part);
    }

    @Transactional
    public PartDTO createPart(PartDTO dto) {
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("Only administrators can create parts");
        }

        Part part = partMapper.toEntity(dto);
        part = partRepository.save(part);
        return partMapper.toDto(part);
    }

    @Transactional
    public PartDTO updatePart(Long id, PartDTO dto) {
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("Only administrators can update parts");
        }

        Part part = partRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Part", "id", id));

        partMapper.updateEntityFromDto(dto, part);
        part = partRepository.save(part);
        return partMapper.toDto(part);
    }

    @Transactional
    public void deletePart(Long id) {
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("Only administrators can delete parts");
        }

        Part part = partRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Part", "id", id));
        partRepository.delete(part);
    }
}







