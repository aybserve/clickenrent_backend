package org.clickenrent.rentalservice.service;

import lombok.RequiredArgsConstructor;
import org.clickenrent.rentalservice.dto.RentalDTO;
import org.clickenrent.rentalservice.entity.Rental;
import org.clickenrent.rentalservice.exception.ResourceNotFoundException;
import org.clickenrent.rentalservice.exception.UnauthorizedException;
import org.clickenrent.rentalservice.mapper.RentalMapper;
import org.clickenrent.rentalservice.repository.RentalRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service for managing Rental entities with security checks.
 */
@Service
@RequiredArgsConstructor
public class RentalService {

    private final RentalRepository rentalRepository;
    private final RentalMapper rentalMapper;
    private final SecurityService securityService;

    @Transactional(readOnly = true)
    public Page<RentalDTO> getAllRentals(Pageable pageable) {
        // Admin can see all rentals
        if (securityService.isAdmin()) {
            return rentalRepository.findAll(pageable)
                    .map(rentalMapper::toDto);
        }

        // B2B can see rentals for their companies
        if (securityService.isB2B()) {
            List<Long> companyIds = securityService.getCurrentUserCompanyIds();
            List<Rental> rentals = rentalRepository.findAll().stream()
                    .filter(rental -> companyIds.contains(rental.getCompanyId()))
                    .toList();
            
            int start = (int) pageable.getOffset();
            int end = Math.min((start + pageable.getPageSize()), rentals.size());
            List<RentalDTO> pageContent = rentals.subList(start, end).stream()
                    .map(rentalMapper::toDto)
                    .toList();
            
            return new PageImpl<>(pageContent, pageable, rentals.size());
        }

        // Customer can only see their own rentals
        if (securityService.isCustomer()) {
            Long currentUserId = securityService.getCurrentUserId();
            if (currentUserId != null) {
                List<RentalDTO> userRentals = rentalRepository.findByUserId(currentUserId).stream()
                        .map(rentalMapper::toDto)
                        .toList();
                return new PageImpl<>(userRentals, pageable, userRentals.size());
            }
        }

        throw new UnauthorizedException("You don't have permission to view rentals");
    }

    @Transactional(readOnly = true)
    public RentalDTO getRentalById(Long id) {
        Rental rental = rentalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rental", "id", id));

        // Check if user has access to this rental
        if (!securityService.isAdmin() && 
            !securityService.hasAccessToUser(rental.getUserId()) &&
            !securityService.hasAccessToCompany(rental.getCompanyId())) {
            throw new UnauthorizedException("You don't have permission to view this rental");
        }

        return rentalMapper.toDto(rental);
    }

    @Transactional
    public RentalDTO createRental(RentalDTO rentalDTO) {
        // User can only create rentals for themselves (unless admin)
        if (!securityService.isAdmin() && !securityService.hasAccessToUser(rentalDTO.getUserId())) {
            throw new UnauthorizedException("You can only create rentals for yourself");
        }

        Rental rental = rentalMapper.toEntity(rentalDTO);
        rental = rentalRepository.save(rental);
        return rentalMapper.toDto(rental);
    }

    @Transactional
    public RentalDTO updateRental(Long id, RentalDTO rentalDTO) {
        Rental rental = rentalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rental", "id", id));

        // Check if user has access to this rental
        if (!securityService.isAdmin() && 
            !securityService.hasAccessToUser(rental.getUserId()) &&
            !securityService.hasAccessToCompany(rental.getCompanyId())) {
            throw new UnauthorizedException("You don't have permission to update this rental");
        }

        rentalMapper.updateEntityFromDto(rentalDTO, rental);
        rental = rentalRepository.save(rental);
        return rentalMapper.toDto(rental);
    }

    @Transactional
    public void deleteRental(Long id) {
        Rental rental = rentalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rental", "id", id));

        // Only admins can delete rentals
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("Only administrators can delete rentals");
        }

        rentalRepository.delete(rental);
    }
}


