package org.clickenrent.rentalservice.service;

import lombok.RequiredArgsConstructor;
import org.clickenrent.rentalservice.dto.BikeRentalDTO;
import org.clickenrent.rentalservice.entity.Bike;
import org.clickenrent.rentalservice.entity.BikeRental;
import org.clickenrent.rentalservice.entity.Rental;
import org.clickenrent.rentalservice.exception.ResourceNotFoundException;
import org.clickenrent.rentalservice.exception.UnauthorizedException;
import org.clickenrent.rentalservice.mapper.BikeRentalMapper;
import org.clickenrent.rentalservice.repository.BikeRentalRepository;
import org.clickenrent.rentalservice.repository.BikeRepository;
import org.clickenrent.rentalservice.repository.RentalRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BikeRentalService {

    private final BikeRentalRepository bikeRentalRepository;
    private final BikeRepository bikeRepository;
    private final RentalRepository rentalRepository;
    private final BikeRentalMapper bikeRentalMapper;
    private final SecurityService securityService;

    @Transactional(readOnly = true)
    public Page<BikeRentalDTO> getAllBikeRentals(Pageable pageable) {
        if (securityService.isAdmin()) {
            return bikeRentalRepository.findAll(pageable)
                    .map(bikeRentalMapper::toDto);
        }

        throw new UnauthorizedException("You don't have permission to view all bike rentals");
    }

    @Transactional(readOnly = true)
    public BikeRentalDTO getBikeRentalById(Long id) {
        BikeRental bikeRental = bikeRentalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("BikeRental", "id", id));

        // Check access
        if (!securityService.isAdmin() && !securityService.hasAccessToUser(bikeRental.getRental().getUserId())) {
            throw new UnauthorizedException("You don't have permission to view this bike rental");
        }

        return bikeRentalMapper.toDto(bikeRental);
    }

    @Transactional
    public BikeRentalDTO createBikeRental(BikeRentalDTO dto) {
        // Validate bike and rental exist
        Bike bike = bikeRepository.findById(dto.getBikeId())
                .orElseThrow(() -> new ResourceNotFoundException("Bike", "id", dto.getBikeId()));
        Rental rental = rentalRepository.findById(dto.getRentalId())
                .orElseThrow(() -> new ResourceNotFoundException("Rental", "id", dto.getRentalId()));

        // Check permissions
        if (!securityService.isAdmin() && !securityService.hasAccessToUser(rental.getUserId())) {
            throw new UnauthorizedException("You don't have permission to create bike rentals");
        }

        BikeRental bikeRental = bikeRentalMapper.toEntity(dto);
        
        // Calculate revenue share if B2B rentable
        if (bike.getIsB2BRentable() && bike.getRevenueSharePercent() != null) {
            bikeRental.setIsRevenueSharePaid(false);
        }

        bikeRental = bikeRentalRepository.save(bikeRental);
        return bikeRentalMapper.toDto(bikeRental);
    }

    @Transactional
    public void deleteBikeRental(Long id) {
        BikeRental bikeRental = bikeRentalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("BikeRental", "id", id));

        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("Only administrators can delete bike rentals");
        }

        bikeRentalRepository.delete(bikeRental);
    }
}
