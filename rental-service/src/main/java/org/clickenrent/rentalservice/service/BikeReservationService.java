package org.clickenrent.rentalservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.clickenrent.contracts.auth.UserDTO;
import org.clickenrent.rentalservice.client.AuthServiceClient;
import org.clickenrent.rentalservice.dto.BikeReservationDTO;
import org.clickenrent.rentalservice.entity.Bike;
import org.clickenrent.rentalservice.entity.BikeReservation;
import org.clickenrent.rentalservice.exception.ResourceNotFoundException;
import org.clickenrent.rentalservice.exception.UnauthorizedException;
import org.clickenrent.rentalservice.mapper.BikeReservationMapper;
import org.clickenrent.rentalservice.repository.BikeRepository;
import org.clickenrent.rentalservice.repository.BikeReservationRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BikeReservationService {

    private final BikeReservationRepository bikeReservationRepository;
    private final BikeRepository bikeRepository;
    private final BikeReservationMapper bikeReservationMapper;
    private final SecurityService securityService;
    private final AuthServiceClient authServiceClient;

    @Transactional(readOnly = true)
    public Page<BikeReservationDTO> getAllReservations(Pageable pageable) {
        if (securityService.isAdmin()) {
            return bikeReservationRepository.findAll(pageable)
                    .map(bikeReservationMapper::toDto);
        }

        throw new UnauthorizedException("You don't have permission to view all reservations");
    }

    @Transactional(readOnly = true)
    public List<BikeReservationDTO> getReservationsByUser(Long userId) {
        if (!securityService.isAdmin() && !securityService.hasAccessToUser(userId)) {
            throw new UnauthorizedException("You don't have permission to view these reservations");
        }

        return bikeReservationRepository.findByUserId(userId).stream()
                .map(bikeReservationMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public BikeReservationDTO getReservationById(Long id) {
        BikeReservation reservation = bikeReservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("BikeReservation", "id", id));

        if (!securityService.isAdmin() && !securityService.hasAccessToUser(reservation.getUserId())) {
            throw new UnauthorizedException("You don't have permission to view this reservation");
        }

        return bikeReservationMapper.toDto(reservation);
    }

    @Transactional(readOnly = true)
    public BikeReservationDTO findByExternalId(String externalId) {
        BikeReservation reservation = bikeReservationRepository.findByExternalId(externalId)
                .orElseThrow(() -> new ResourceNotFoundException("BikeReservation", "externalId", externalId));

        if (!securityService.isAdmin() && !securityService.hasAccessToUser(reservation.getUserId())) {
            throw new UnauthorizedException("You don't have permission to view this reservation");
        }

        return bikeReservationMapper.toDto(reservation);
    }

    @Transactional
    public BikeReservationDTO updateByExternalId(String externalId, BikeReservationDTO dto) {
        BikeReservation reservation = bikeReservationRepository.findByExternalId(externalId)
                .orElseThrow(() -> new ResourceNotFoundException("BikeReservation", "externalId", externalId));

        if (!securityService.isAdmin() && !securityService.hasAccessToUser(reservation.getUserId())) {
            throw new UnauthorizedException("You don't have permission to update this reservation");
        }

        // Update fields
        if (dto.getStartDateTime() != null) {
            reservation.setStartDateTime(dto.getStartDateTime());
        }
        if (dto.getEndDateTime() != null) {
            reservation.setEndDateTime(dto.getEndDateTime());
        }
        
        reservation = bikeReservationRepository.save(reservation);
        log.info("Updated bike reservation by externalId: {}", externalId);
        return bikeReservationMapper.toDto(reservation);
    }

    @Transactional
    public void deleteByExternalId(String externalId) {
        BikeReservation reservation = bikeReservationRepository.findByExternalId(externalId)
                .orElseThrow(() -> new ResourceNotFoundException("BikeReservation", "externalId", externalId));

        if (!securityService.isAdmin() && !securityService.hasAccessToUser(reservation.getUserId())) {
            throw new UnauthorizedException("You can only delete your own reservations");
        }

        bikeReservationRepository.delete(reservation);
        log.info("Deleted bike reservation by externalId: {}", externalId);
    }

    @Transactional
    public BikeReservationDTO createReservation(BikeReservationDTO dto) {
        // Users can only create reservations for themselves (unless admin)
        if (!securityService.isAdmin() && !securityService.hasAccessToUser(dto.getUserId())) {
            throw new UnauthorizedException("You can only create reservations for yourself");
        }

        // Validate bike exists
        Bike bike = bikeRepository.findById(dto.getBikeId())
                .orElseThrow(() -> new ResourceNotFoundException("Bike", "id", dto.getBikeId()));

        // TODO: Check for overlapping reservations

        BikeReservation reservation = bikeReservationMapper.toEntity(dto);
        
        // DUAL-WRITE: Populate userExternalId
        if (dto.getUserId() != null) {
            try {
                UserDTO user = authServiceClient.getUserById(dto.getUserId());
                reservation.setUserId(dto.getUserId());
                reservation.setUserExternalId(user.getExternalId());
                log.debug("Populated userExternalId: {} for bike reservation", user.getExternalId());
            } catch (Exception e) {
                log.error("Failed to fetch user external ID for userId: {}", dto.getUserId(), e);
                throw new RuntimeException("Failed to fetch user details", e);
            }
        }
        
        reservation = bikeReservationRepository.save(reservation);
        return bikeReservationMapper.toDto(reservation);
    }

    @Transactional
    public void deleteReservation(Long id) {
        BikeReservation reservation = bikeReservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("BikeReservation", "id", id));

        if (!securityService.isAdmin() && !securityService.hasAccessToUser(reservation.getUserId())) {
            throw new UnauthorizedException("You can only delete your own reservations");
        }

        bikeReservationRepository.delete(reservation);
    }

    @Transactional(readOnly = true)
    public boolean existsByExternalId(String externalId) {
        return bikeReservationRepository.existsByExternalId(externalId);
    }
}


