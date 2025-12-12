package org.clickenrent.rentalservice.service;

import lombok.RequiredArgsConstructor;
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
public class BikeReservationService {

    private final BikeReservationRepository bikeReservationRepository;
    private final BikeRepository bikeRepository;
    private final BikeReservationMapper bikeReservationMapper;
    private final SecurityService securityService;

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
}
