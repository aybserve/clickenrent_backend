package org.clickenrent.rentalservice.service;

import lombok.RequiredArgsConstructor;
import org.clickenrent.rentalservice.dto.BikePartDTO;
import org.clickenrent.rentalservice.entity.Bike;
import org.clickenrent.rentalservice.entity.BikePart;
import org.clickenrent.rentalservice.entity.Part;
import org.clickenrent.rentalservice.exception.ResourceNotFoundException;
import org.clickenrent.rentalservice.exception.UnauthorizedException;
import org.clickenrent.rentalservice.mapper.BikePartMapper;
import org.clickenrent.rentalservice.repository.BikePartRepository;
import org.clickenrent.rentalservice.repository.BikeRepository;
import org.clickenrent.rentalservice.repository.PartRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BikePartService {

    private final BikePartRepository bikePartRepository;
    private final BikeRepository bikeRepository;
    private final PartRepository partRepository;
    private final BikePartMapper bikePartMapper;
    private final SecurityService securityService;

    @Transactional(readOnly = true)
    public Page<BikePartDTO> getAllBikeParts(Pageable pageable) {
        if (securityService.isAdmin()) {
            return bikePartRepository.findAll(pageable)
                    .map(bikePartMapper::toDto);
        }

        throw new UnauthorizedException("You don't have permission to view all bike parts");
    }

    @Transactional(readOnly = true)
    public BikePartDTO getBikePartById(Long id) {
        BikePart bikePart = bikePartRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("BikePart", "id", id));

        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("You don't have permission to view this bike part");
        }

        return bikePartMapper.toDto(bikePart);
    }

    @Transactional
    public BikePartDTO createBikePart(BikePartDTO dto) {
        // Validate bike and part exist
        Bike bike = bikeRepository.findById(dto.getBikeId())
                .orElseThrow(() -> new ResourceNotFoundException("Bike", "id", dto.getBikeId()));
        Part part = partRepository.findById(dto.getPartId())
                .orElseThrow(() -> new ResourceNotFoundException("Part", "id", dto.getPartId()));

        // Check permissions
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("You don't have permission to create bike parts");
        }

        BikePart bikePart = bikePartMapper.toEntity(dto);
        bikePart = bikePartRepository.save(bikePart);
        return bikePartMapper.toDto(bikePart);
    }

    @Transactional
    public void deleteBikePart(Long id) {
        BikePart bikePart = bikePartRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("BikePart", "id", id));

        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("Only administrators can delete bike parts");
        }

        bikePartRepository.delete(bikePart);
    }
}


