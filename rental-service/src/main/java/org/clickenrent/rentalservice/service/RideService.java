package org.clickenrent.rentalservice.service;

import lombok.RequiredArgsConstructor;
import org.clickenrent.rentalservice.dto.RideDTO;
import org.clickenrent.rentalservice.entity.BikeRental;
import org.clickenrent.rentalservice.entity.Ride;
import org.clickenrent.rentalservice.entity.RideStatus;
import org.clickenrent.rentalservice.exception.ResourceNotFoundException;
import org.clickenrent.rentalservice.exception.UnauthorizedException;
import org.clickenrent.rentalservice.mapper.RideMapper;
import org.clickenrent.rentalservice.repository.BikeRentalRepository;
import org.clickenrent.rentalservice.repository.RideRepository;
import org.clickenrent.rentalservice.repository.RideStatusRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RideService {

    private final RideRepository rideRepository;
    private final BikeRentalRepository bikeRentalRepository;
    private final RideStatusRepository rideStatusRepository;
    private final RideMapper rideMapper;
    private final SecurityService securityService;

    @Transactional(readOnly = true)
    public Page<RideDTO> getAllRides(Pageable pageable) {
        if (securityService.isAdmin()) {
            return rideRepository.findAll(pageable)
                    .map(rideMapper::toDto);
        }

        throw new UnauthorizedException("You don't have permission to view all rides");
    }

    @Transactional(readOnly = true)
    public List<RideDTO> getRidesByBikeRental(Long bikeRentalId) {
        BikeRental bikeRental = bikeRentalRepository.findById(bikeRentalId)
                .orElseThrow(() -> new ResourceNotFoundException("BikeRental", "id", bikeRentalId));

        // Check access
        if (!securityService.isAdmin() && !securityService.hasAccessToUser(bikeRental.getRental().getUserId())) {
            throw new UnauthorizedException("You don't have permission to view rides for this bike rental");
        }

        return rideRepository.findByBikeRental(bikeRental).stream()
                .map(rideMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public RideDTO getRideById(Long id) {
        Ride ride = rideRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ride", "id", id));

        // Check access
        if (!securityService.isAdmin() && !securityService.hasAccessToUser(ride.getBikeRental().getRental().getUserId())) {
            throw new UnauthorizedException("You don't have permission to view this ride");
        }

        return rideMapper.toDto(ride);
    }

    @Transactional
    public RideDTO startRide(RideDTO dto) {
        BikeRental bikeRental = bikeRentalRepository.findById(dto.getBikeRentalId())
                .orElseThrow(() -> new ResourceNotFoundException("BikeRental", "id", dto.getBikeRentalId()));

        // Check access
        if (!securityService.isAdmin() && !securityService.hasAccessToUser(bikeRental.getRental().getUserId())) {
            throw new UnauthorizedException("You don't have permission to start this ride");
        }

        Ride ride = rideMapper.toEntity(dto);
        if (ride.getStartDateTime() == null) {
            ride.setStartDateTime(LocalDateTime.now());
        }

        // Set ride status to Active
        RideStatus activeStatus = rideStatusRepository.findByName("Active")
                .orElseThrow(() -> new ResourceNotFoundException("RideStatus", "name", "Active"));
        ride.setRideStatus(activeStatus);

        ride = rideRepository.save(ride);
        return rideMapper.toDto(ride);
    }

    @Transactional
    public RideDTO endRide(Long id, RideDTO dto) {
        Ride ride = rideRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ride", "id", id));

        // Check access
        if (!securityService.isAdmin() && !securityService.hasAccessToUser(ride.getBikeRental().getRental().getUserId())) {
            throw new UnauthorizedException("You don't have permission to end this ride");
        }

        ride.setEndDateTime(LocalDateTime.now());

        // Set ride status to Finished
        RideStatus finishedStatus = rideStatusRepository.findByName("Finished")
                .orElseThrow(() -> new ResourceNotFoundException("RideStatus", "name", "Finished"));
        ride.setRideStatus(finishedStatus);

        if (dto.getEndLocationId() != null) {
            rideMapper.updateEntityFromDto(dto, ride);
        }

        ride = rideRepository.save(ride);
        return rideMapper.toDto(ride);
    }

    @Transactional
    public void deleteRide(Long id) {
        Ride ride = rideRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ride", "id", id));

        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("Only administrators can delete rides");
        }

        rideRepository.delete(ride);
    }
}




