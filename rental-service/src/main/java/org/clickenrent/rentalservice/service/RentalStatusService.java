package org.clickenrent.rentalservice.service;

import lombok.RequiredArgsConstructor;
import org.clickenrent.rentalservice.dto.RentalStatusDTO;
import org.clickenrent.rentalservice.entity.RentalStatus;
import org.clickenrent.rentalservice.exception.ResourceNotFoundException;
import org.clickenrent.rentalservice.exception.UnauthorizedException;
import org.clickenrent.rentalservice.mapper.RentalStatusMapper;
import org.clickenrent.rentalservice.repository.RentalStatusRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RentalStatusService {

    private final RentalStatusRepository rentalStatusRepository;
    private final RentalStatusMapper rentalStatusMapper;
    private final SecurityService securityService;

    @Transactional(readOnly = true)
    public List<RentalStatusDTO> getAllStatuses() {
        return rentalStatusRepository.findAll().stream()
                .map(rentalStatusMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public RentalStatusDTO getStatusById(Long id) {
        RentalStatus status = rentalStatusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RentalStatus", "id", id));
        return rentalStatusMapper.toDto(status);
    }

    @Transactional
    public RentalStatusDTO createStatus(RentalStatusDTO dto) {
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("Only administrators can create rental statuses");
        }

        RentalStatus status = rentalStatusMapper.toEntity(dto);
        status = rentalStatusRepository.save(status);
        return rentalStatusMapper.toDto(status);
    }

    @Transactional
    public RentalStatusDTO updateStatus(Long id, RentalStatusDTO dto) {
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("Only administrators can update rental statuses");
        }

        RentalStatus status = rentalStatusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RentalStatus", "id", id));

        rentalStatusMapper.updateEntityFromDto(dto, status);
        status = rentalStatusRepository.save(status);
        return rentalStatusMapper.toDto(status);
    }

    @Transactional
    public void deleteStatus(Long id) {
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("Only administrators can delete rental statuses");
        }

        RentalStatus status = rentalStatusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RentalStatus", "id", id));
        rentalStatusRepository.delete(status);
    }
}


