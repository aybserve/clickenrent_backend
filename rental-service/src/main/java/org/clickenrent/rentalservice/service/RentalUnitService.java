package org.clickenrent.rentalservice.service;

import lombok.RequiredArgsConstructor;
import org.clickenrent.rentalservice.dto.RentalUnitDTO;
import org.clickenrent.rentalservice.entity.RentalUnit;
import org.clickenrent.rentalservice.exception.ResourceNotFoundException;
import org.clickenrent.rentalservice.exception.UnauthorizedException;
import org.clickenrent.rentalservice.mapper.RentalUnitMapper;
import org.clickenrent.rentalservice.repository.RentalUnitRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RentalUnitService {

    private final RentalUnitRepository rentalUnitRepository;
    private final RentalUnitMapper rentalUnitMapper;
    private final SecurityService securityService;

    @Transactional(readOnly = true)
    public List<RentalUnitDTO> getAllUnits() {
        return rentalUnitRepository.findAll().stream()
                .map(rentalUnitMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public RentalUnitDTO getUnitById(Long id) {
        RentalUnit unit = rentalUnitRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RentalUnit", "id", id));
        return rentalUnitMapper.toDto(unit);
    }

    @Transactional
    public RentalUnitDTO createUnit(RentalUnitDTO dto) {
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("Only administrators can create rental units");
        }

        RentalUnit unit = rentalUnitMapper.toEntity(dto);
        unit = rentalUnitRepository.save(unit);
        return rentalUnitMapper.toDto(unit);
    }

    @Transactional
    public void deleteUnit(Long id) {
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("Only administrators can delete rental units");
        }

        RentalUnit unit = rentalUnitRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RentalUnit", "id", id));
        rentalUnitRepository.delete(unit);
    }
}








