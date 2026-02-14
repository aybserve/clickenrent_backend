package org.clickenrent.rentalservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.clickenrent.contracts.notification.SendNotificationRequest;
import org.clickenrent.rentalservice.client.NotificationClient;
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

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class RideService {

    private final RideRepository rideRepository;
    private final BikeRentalRepository bikeRentalRepository;
    private final RideStatusRepository rideStatusRepository;
    private final RideMapper rideMapper;
    private final SecurityService securityService;
    private final NotificationClient notificationClient;

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
        if (!securityService.isAdmin() && !securityService.hasAccessToUserByExternalId(bikeRental.getRental().getUserExternalId())) {
            throw new UnauthorizedException("You don't have permission to view rides for this bike rental");
        }

        return rideRepository.findByBikeRental(bikeRental).stream()
                .map(rideMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<RideDTO> getRidesByBikeRentalExternalId(String bikeRentalExternalId) {
        BikeRental bikeRental = bikeRentalRepository.findByExternalId(bikeRentalExternalId)
                .orElseThrow(() -> new ResourceNotFoundException("BikeRental", "externalId", bikeRentalExternalId));

        // Check access
        if (!securityService.isAdmin() && !securityService.hasAccessToUserByExternalId(bikeRental.getRental().getUserExternalId())) {
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
        if (!securityService.isAdmin() && !securityService.hasAccessToUserByExternalId(ride.getBikeRental().getRental().getUserExternalId())) {
            throw new UnauthorizedException("You don't have permission to view this ride");
        }

        return rideMapper.toDto(ride);
    }

    @Transactional
    public RideDTO startRide(RideDTO dto) {
        BikeRental bikeRental = bikeRentalRepository.findById(dto.getBikeRentalId())
                .orElseThrow(() -> new ResourceNotFoundException("BikeRental", "id", dto.getBikeRentalId()));

        // Check access
        if (!securityService.isAdmin() && !securityService.hasAccessToUserByExternalId(bikeRental.getRental().getUserExternalId())) {
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

        ride.sanitizeForCreate();
        ride = rideRepository.save(ride);

        // Send notification
        sendNotificationAsync(
                bikeRental,
                "RIDE_STARTED",
                "Ride Started 🚴‍♂️",
                "Your ride has begun! Enjoy your journey!",
                Map.of(
                        "rideId", ride.getId(),
                        "bikeRentalId", bikeRental.getId(),
                        "startTime", ride.getStartDateTime().toString()
                )
        );

        return rideMapper.toDto(ride);
    }

    @Transactional
    public RideDTO endRide(Long id, RideDTO dto) {
        Ride ride = rideRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ride", "id", id));

        // Check access
        if (!securityService.isAdmin() && !securityService.hasAccessToUserByExternalId(ride.getBikeRental().getRental().getUserExternalId())) {
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

        // Calculate ride duration
        Duration duration = Duration.between(ride.getStartDateTime(), ride.getEndDateTime());
        long minutes = duration.toMinutes();

        // Send notification
        sendNotificationAsync(
                ride.getBikeRental(),
                "RIDE_ENDED",
                "Ride Completed ✅",
                String.format("Your ride is complete! Duration: %d minutes. Thank you!", minutes),
                Map.of(
                        "rideId", ride.getId(),
                        "bikeRentalId", ride.getBikeRental().getId(),
                        "duration", minutes,
                        "startTime", ride.getStartDateTime().toString(),
                        "endTime", ride.getEndDateTime().toString()
                )
        );

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

    /**
     * Send notification asynchronously without blocking the main operation.
     * Uses userExternalId directly from Rental entity - no conversion needed!
     *
     * @param bikeRental       The bike rental
     * @param notificationType Notification type
     * @param title            Notification title
     * @param body             Notification body
     * @param data             Additional data
     */
    private void sendNotificationAsync(
            BikeRental bikeRental,
            String notificationType,
            String title,
            String body,
            Map<String, Object> data
    ) {
        try {
            String userExternalId = bikeRental.getRental().getUserExternalId();

            SendNotificationRequest request = SendNotificationRequest.builder()
                    .userExternalId(userExternalId)
                    .notificationType(notificationType)
                    .title(title)
                    .body(body)
                    .data(data)
                    .priority("high")
                    .build();

            notificationClient.sendNotification(request);
            log.debug("Sent notification for user: {}, type: {}", userExternalId, notificationType);
        } catch (Exception e) {
            // Don't fail the main operation if notification fails
            log.error("Failed to send notification for bike rental: {}", bikeRental.getId(), e);
        }
    }

    @Transactional(readOnly = true)
    public RideDTO getRideByExternalId(String externalId) {
        Ride ride = rideRepository.findByExternalId(externalId)
                .orElseThrow(() -> new ResourceNotFoundException("Ride", "externalId", externalId));
        return rideMapper.toDto(ride);
    }
}




