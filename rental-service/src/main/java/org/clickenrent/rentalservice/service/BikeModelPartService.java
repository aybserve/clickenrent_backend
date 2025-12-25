package org.clickenrent.rentalservice.service;

import lombok.RequiredArgsConstructor;
import org.clickenrent.rentalservice.dto.BikeModelPartDTO;
import org.clickenrent.rentalservice.entity.BikeModel;
import org.clickenrent.rentalservice.entity.BikeModelPart;
import org.clickenrent.rentalservice.entity.Part;
import org.clickenrent.rentalservice.exception.ResourceNotFoundException;
import org.clickenrent.rentalservice.exception.UnauthorizedException;
import org.clickenrent.rentalservice.mapper.BikeModelPartMapper;
import org.clickenrent.rentalservice.repository.BikeModelPartRepository;
import org.clickenrent.rentalservice.repository.BikeModelRepository;
import org.clickenrent.rentalservice.repository.PartRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BikeModelPartService {

    private final BikeModelPartRepository bikeModelPartRepository;
    private final BikeModelRepository bikeModelRepository;
    private final PartRepository partRepository;
    private final BikeModelPartMapper bikeModelPartMapper;
    private final SecurityService securityService;

    @Transactional(readOnly = true)
    public Page<BikeModelPartDTO> getAllBikeModelParts(Pageable pageable) {
        if (securityService.isAdmin()) {
            return bikeModelPartRepository.findAll(pageable)
                    .map(bikeModelPartMapper::toDto);
        }

        throw new UnauthorizedException("You don't have permission to view all bike model parts");
    }

    @Transactional(readOnly = true)
    public BikeModelPartDTO getBikeModelPartById(Long id) {
        BikeModelPart bikeModelPart = bikeModelPartRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("BikeModelPart", "id", id));

        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("You don't have permission to view this bike model part");
        }

        return bikeModelPartMapper.toDto(bikeModelPart);
    }

    @Transactional
    public BikeModelPartDTO createBikeModelPart(BikeModelPartDTO dto) {
        // Validate bike model and part exist
        BikeModel bikeModel = bikeModelRepository.findById(dto.getBikeModelId())
                .orElseThrow(() -> new ResourceNotFoundException("BikeModel", "id", dto.getBikeModelId()));
        Part part = partRepository.findById(dto.getPartId())
                .orElseThrow(() -> new ResourceNotFoundException("Part", "id", dto.getPartId()));

        // Check permissions
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("You don't have permission to create bike model parts");
        }

        BikeModelPart bikeModelPart = bikeModelPartMapper.toEntity(dto);
        bikeModelPart = bikeModelPartRepository.save(bikeModelPart);
        return bikeModelPartMapper.toDto(bikeModelPart);
    }

    @Transactional
    public void deleteBikeModelPart(Long id) {
        BikeModelPart bikeModelPart = bikeModelPartRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("BikeModelPart", "id", id));

        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("Only administrators can delete bike model parts");
        }

        bikeModelPartRepository.delete(bikeModelPart);
    }
}



