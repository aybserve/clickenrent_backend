package org.clickenrent.rentalservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.clickenrent.contracts.auth.CompanyDTO;
import org.clickenrent.contracts.auth.UserDTO;
import org.clickenrent.rentalservice.client.AuthServiceClient;
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
@Slf4j
public class RentalService {

    private final RentalRepository rentalRepository;
    private final RentalMapper rentalMapper;
    private final SecurityService securityService;
    private final AuthServiceClient authServiceClient;

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

    /**
     * Find rental by externalId for cross-service communication
     */
    @Transactional(readOnly = true)
    public RentalDTO findByExternalId(String externalId) {
        Rental rental = rentalRepository.findByExternalId(externalId)
                .orElseThrow(() -> new ResourceNotFoundException("Rental", "externalId", externalId));
        return rentalMapper.toDto(rental);
    }

    /**
     * Check if rental exists by externalId
     */
    @Transactional(readOnly = true)
    public boolean existsByExternalId(String externalId) {
        return rentalRepository.existsByExternalId(externalId);
    }

    @Transactional
    public RentalDTO updateByExternalId(String externalId, RentalDTO dto) {
        Rental rental = rentalRepository.findByExternalId(externalId)
                .orElseThrow(() -> new ResourceNotFoundException("Rental", "externalId", externalId));
        
        if (!securityService.isAdmin() && !securityService.hasAccessToUser(rental.getUserId())) {
            throw new UnauthorizedException("You don't have permission to update this rental");
        }
        
        rentalMapper.updateEntityFromDto(dto, rental);
        rental = rentalRepository.save(rental);
        log.info("Updated rental by externalId: {}", externalId);
        return rentalMapper.toDto(rental);
    }

    @Transactional
    public void deleteByExternalId(String externalId) {
        Rental rental = rentalRepository.findByExternalId(externalId)
                .orElseThrow(() -> new ResourceNotFoundException("Rental", "externalId", externalId));
        
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("Only admins can delete rentals");
        }
        
        rentalRepository.delete(rental);
        log.info("Deleted rental by externalId: {}", externalId);
    }

    @Transactional
    public RentalDTO createRental(RentalDTO rentalDTO) {
        // User can only create rentals for themselves (unless admin)
        if (!securityService.isAdmin() && !securityService.hasAccessToUser(rentalDTO.getUserId())) {
            throw new UnauthorizedException("You can only create rentals for yourself");
        }

        Rental rental = rentalMapper.toEntity(rentalDTO);
        
        // DUAL-WRITE: Fetch and populate externalIds
        if (rentalDTO.getUserId() != null) {
            try {
                UserDTO user = authServiceClient.getUserById(rentalDTO.getUserId());
                rental.setUserId(rentalDTO.getUserId());
                rental.setUserExternalId(user.getExternalId());
                log.debug("Populated userExternalId: {} for rental", user.getExternalId());
            } catch (Exception e) {
                log.error("Failed to fetch user external ID for userId: {}", rentalDTO.getUserId(), e);
                throw new RuntimeException("Failed to fetch user details", e);
            }
        }
        
        if (rentalDTO.getCompanyId() != null) {
            try {
                CompanyDTO company = authServiceClient.getCompanyById(rentalDTO.getCompanyId());
                rental.setCompanyId(rentalDTO.getCompanyId());
                rental.setCompanyExternalId(company.getExternalId());
                log.debug("Populated companyExternalId: {} for rental", company.getExternalId());
            } catch (Exception e) {
                log.error("Failed to fetch company external ID for companyId: {}", rentalDTO.getCompanyId(), e);
                throw new RuntimeException("Failed to fetch company details", e);
            }
        }
        
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




