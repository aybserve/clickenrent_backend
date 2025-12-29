package org.clickenrent.rentalservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.clickenrent.contracts.notification.SendNotificationRequest;
import org.clickenrent.rentalservice.client.NotificationClient;
import org.clickenrent.rentalservice.dto.*;
import org.clickenrent.rentalservice.entity.*;
import org.clickenrent.rentalservice.exception.PhotoAlreadyExistsException;
import org.clickenrent.rentalservice.exception.ResourceNotFoundException;
import org.clickenrent.rentalservice.exception.UnauthorizedException;
import org.clickenrent.rentalservice.mapper.BikeRentalMapper;
import org.clickenrent.rentalservice.repository.BikeRentalRepository;
import org.clickenrent.rentalservice.repository.BikeRepository;
import org.clickenrent.rentalservice.repository.LockRepository;
import org.clickenrent.rentalservice.repository.RentalRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class BikeRentalService {

    private final BikeRentalRepository bikeRentalRepository;
    private final BikeRepository bikeRepository;
    private final RentalRepository rentalRepository;
    private final LockRepository lockRepository;
    private final BikeRentalMapper bikeRentalMapper;
    private final SecurityService securityService;
    private final LockEncryptionService lockEncryptionService;
    private final LockStatusService lockStatusService;
    private final CoordinatesService coordinatesService;
    private final AzureBlobStorageService azureBlobStorageService;
    private final PhotoValidationService photoValidationService;
    private final NotificationClient notificationClient;

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
        if (!securityService.isAdmin() && !securityService.hasAccessToUserByExternalId(bikeRental.getRental().getUserExternalId())) {
            throw new UnauthorizedException("You don't have permission to view this bike rental");
        }

        return bikeRentalMapper.toDto(bikeRental);
    }

    @Transactional(readOnly = true)
    public BikeRentalDTO findByExternalId(String externalId) {
        BikeRental bikeRental = bikeRentalRepository.findByExternalId(externalId)
                .orElseThrow(() -> new ResourceNotFoundException("BikeRental", "externalId", externalId));
        
        // Security check - verify access through rental
        if (bikeRental.getRental() != null) {
            if (!securityService.isAdmin() && !securityService.hasAccessToUserByExternalId(bikeRental.getRental().getUserExternalId())) {
                throw new UnauthorizedException("You don't have permission to view this bike rental");
            }
        }
        
        return bikeRentalMapper.toDto(bikeRental);
    }

    @Transactional
    public BikeRentalDTO updateByExternalId(String externalId, BikeRentalDTO dto) {
        BikeRental bikeRental = bikeRentalRepository.findByExternalId(externalId)
                .orElseThrow(() -> new ResourceNotFoundException("BikeRental", "externalId", externalId));
        
        // Security check
        if (bikeRental.getRental() != null) {
            if (!securityService.isAdmin() && !securityService.hasAccessToUserByExternalId(bikeRental.getRental().getUserExternalId())) {
                throw new UnauthorizedException("You don't have permission to update this bike rental");
            }
        }
        
        // Update fields
        if (dto.getStartDateTime() != null) bikeRental.setStartDateTime(dto.getStartDateTime());
        if (dto.getEndDateTime() != null) bikeRental.setEndDateTime(dto.getEndDateTime());
        if (dto.getPrice() != null) bikeRental.setPrice(dto.getPrice());
        if (dto.getTotalPrice() != null) bikeRental.setTotalPrice(dto.getTotalPrice());
        
        bikeRental = bikeRentalRepository.save(bikeRental);
        log.info("Updated bike rental by externalId: {}", externalId);
        return bikeRentalMapper.toDto(bikeRental);
    }

    @Transactional
    public void deleteByExternalId(String externalId) {
        BikeRental bikeRental = bikeRentalRepository.findByExternalId(externalId)
                .orElseThrow(() -> new ResourceNotFoundException("BikeRental", "externalId", externalId));
        
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("Only admins can delete bike rentals");
        }
        
        bikeRentalRepository.delete(bikeRental);
        log.info("Deleted bike rental by externalId: {}", externalId);
    }

    @Transactional
    public BikeRentalDTO createBikeRental(BikeRentalDTO dto) {
        // Validate bike and rental exist
        Bike bike = bikeRepository.findById(dto.getBikeId())
                .orElseThrow(() -> new ResourceNotFoundException("Bike", "id", dto.getBikeId()));
        Rental rental = rentalRepository.findById(dto.getRentalId())
                .orElseThrow(() -> new ResourceNotFoundException("Rental", "id", dto.getRentalId()));

        // Check permissions
        if (!securityService.isAdmin() && !securityService.hasAccessToUserByExternalId(rental.getUserExternalId())) {
            throw new UnauthorizedException("You don't have permission to create bike rentals");
        }

        BikeRental bikeRental = bikeRentalMapper.toEntity(dto);
        
        // DUAL-WRITE: Populate cross-service externalId reference fields
        if (bike.getExternalId() != null) {
            bikeRental.setBikeExternalId(bike.getExternalId());
            log.debug("Populated bikeExternalId: {} for bike rental", bike.getExternalId());
        }
        
        if (bikeRental.getLocation() != null && bikeRental.getLocation().getExternalId() != null) {
            bikeRental.setLocationExternalId(bikeRental.getLocation().getExternalId());
            log.debug("Populated locationExternalId: {} for bike rental", bikeRental.getLocation().getExternalId());
        }
        
        if (rental.getExternalId() != null) {
            bikeRental.setRentalExternalId(rental.getExternalId());
            log.debug("Populated rentalExternalId: {} for bike rental", rental.getExternalId());
        }
        
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

    @Transactional
    public UnlockResponseDTO unlockBike(Long bikeRentalId, UnlockRequestDTO request) {
        // Fetch bike rental with bike and lock
        BikeRental bikeRental = bikeRentalRepository.findById(bikeRentalId)
                .orElseThrow(() -> new ResourceNotFoundException("BikeRental", "id", bikeRentalId));

        // Verify user authorization
        if (!securityService.isAdmin() && !securityService.hasAccessToUserByExternalId(bikeRental.getRental().getUserExternalId())) {
            throw new UnauthorizedException("You don't have permission to unlock this bike");
        }

        // Verify bike matches request
        if (!bikeRental.getBike().getId().equals(request.getBikeId())) {
            throw new IllegalArgumentException("Bike ID does not match the rental");
        }

        // Check if bike has a lock
        Lock lock = bikeRental.getBike().getLock();
        if (lock == null) {
            throw new IllegalStateException("This bike does not have a lock assigned");
        }

        // Check if lock has a provider
        if (lock.getLockProvider() == null) {
            throw new IllegalStateException("Lock does not have a provider configured");
        }

        // Generate unlock token
        String unlockToken = lockEncryptionService.generateUnlockToken(bikeRental, lock);

        // Update lock status and last seen
        LockStatus unlockedStatus = lockStatusService.getLockStatusByName("unlocked");
        lock.setLockStatus(unlockedStatus);
        lock.setLastSeenAt(LocalDateTime.now());
        lockRepository.save(lock);

        // Send notification
        sendNotificationAsync(
                bikeRental,
                "BIKE_UNLOCKED",
                "Bike Unlocked 🚴",
                "Your bike has been unlocked. Have a great ride!",
                Map.of(
                        "bikeRentalId", bikeRentalId,
                        "bikeId", bikeRental.getBike().getId(),
                        "bikeExternalId", bikeRental.getBike().getExternalId() != null ? bikeRental.getBike().getExternalId() : ""
                )
        );

        // Return response
        return UnlockResponseDTO.builder()
                .unlockToken(unlockToken)
                .lockId(lock.getExternalId() != null ? lock.getExternalId() : lock.getId().toString())
                .expiresIn(lockEncryptionService.getTokenExpirationSeconds())
                .algorithm("AES-256")
                .build();
    }

    @Transactional
    public LockResponseDTO lockBike(Long bikeRentalId, LockRequestDTO request) {
        // Fetch bike rental
        BikeRental bikeRental = bikeRentalRepository.findById(bikeRentalId)
                .orElseThrow(() -> new ResourceNotFoundException("BikeRental", "id", bikeRentalId));

        // Verify user authorization
        if (!securityService.isAdmin() && !securityService.hasAccessToUserByExternalId(bikeRental.getRental().getUserExternalId())) {
            throw new UnauthorizedException("You don't have permission to lock this bike");
        }

        // Verify bike matches request
        if (!bikeRental.getBike().getId().equals(request.getBikeId())) {
            throw new IllegalArgumentException("Bike ID does not match the rental");
        }

        // Verify lock confirmation
        if (!request.getLockConfirmed()) {
            throw new IllegalArgumentException("Lock must be confirmed");
        }

        // Get lock
        Lock lock = bikeRental.getBike().getLock();
        if (lock == null) {
            throw new IllegalStateException("This bike does not have a lock assigned");
        }

        // Update lock status and last seen
        LockStatus lockedStatus = lockStatusService.getLockStatusByName("locked");
        lock.setLockStatus(lockedStatus);
        lock.setLastSeenAt(LocalDateTime.now());
        lockRepository.save(lock);

        // Update bike coordinates if provided
        if (request.getCoordinates() != null) {
            Coordinates coordinates = coordinatesService.createOrUpdateCoordinates(
                    bikeRental.getBike().getCoordinates(),
                    request.getCoordinates()
            );
            bikeRental.getBike().setCoordinates(coordinates);
            bikeRepository.save(bikeRental.getBike());
        }

        // Get rental status (could be "paused" if applicable)
        String bikeRentalStatus = bikeRental.getBikeRentalStatus() != null
                ? bikeRental.getBikeRentalStatus().getName() 
                : "active";

        // Send notification
        sendNotificationAsync(
                bikeRental,
                "BIKE_LOCKED",
                "Bike Locked 🔒",
                "Your bike has been securely locked.",
                Map.of(
                        "bikeRentalId", bikeRentalId,
                        "bikeId", bikeRental.getBike().getId(),
                        "bikeRentalStatus", bikeRentalStatus
                )
        );

        return LockResponseDTO.builder()
                .success(true)
                .bikeRentalStatus(bikeRentalStatus)
                .build();
    }

    @Transactional(readOnly = true)
    public boolean existsByExternalId(String externalId) {
        return bikeRentalRepository.existsByExternalId(externalId);
    }

    /**
     * Get all bike rentals for a specific rental
     */
    @Transactional(readOnly = true)
    public List<BikeRentalDTO> getBikeRentalsByRentalId(Long rentalId) {
        Rental rental = rentalRepository.findById(rentalId)
                .orElseThrow(() -> new ResourceNotFoundException("Rental", "id", rentalId));

        // Check access
        if (!securityService.isAdmin() && !securityService.hasAccessToUserByExternalId(rental.getUserExternalId())) {
            throw new UnauthorizedException("You don't have permission to view these bike rentals");
        }

        return bikeRentalRepository.findByRental(rental).stream()
                .map(bikeRentalMapper::toDto)
                .toList();
    }

    /**
     * Get all bike rentals for a specific rental by external ID
     */
    @Transactional(readOnly = true)
    public List<BikeRentalDTO> getBikeRentalsByRentalExternalId(String rentalExternalId) {
        Rental rental = rentalRepository.findByExternalId(rentalExternalId)
                .orElseThrow(() -> new ResourceNotFoundException("Rental", "externalId", rentalExternalId));

        // Check access
        if (!securityService.isAdmin() && !securityService.hasAccessToUserByExternalId(rental.getUserExternalId())) {
            throw new UnauthorizedException("You don't have permission to view these bike rentals");
        }

        return bikeRentalRepository.findByRental(rental).stream()
                .map(bikeRentalMapper::toDto)
                .toList();
    }

    /**
     * Upload photo for a bike rental.
     *
     * @param bikeRentalId the bike rental ID
     * @param file the photo file to upload
     * @return response with photo URL
     * @throws ResourceNotFoundException if bike rental not found
     * @throws UnauthorizedException if user doesn't have permission
     * @throws PhotoAlreadyExistsException if photo already exists
     * @throws IllegalStateException if bike rental is not ended
     */
    @Transactional
    public PhotoUploadResponseDTO uploadPhoto(Long bikeRentalId, MultipartFile file) {
        // 1. Check bikeRental exists
        BikeRental bikeRental = bikeRentalRepository.findById(bikeRentalId)
                .orElseThrow(() -> new ResourceNotFoundException("BikeRental", "id", bikeRentalId));

        // 2. Check bikeRental is ended (via bikeRentalStatus)
        if (bikeRental.getBikeRentalStatus() == null || 
            !isRentalEnded(bikeRental.getBikeRentalStatus().getName())) {
            throw new IllegalStateException(
                    String.format("Cannot upload photo for bike rental that is not completed. Current status: %s",
                            bikeRental.getBikeRentalStatus() != null ? bikeRental.getBikeRentalStatus().getName() : "unknown")
            );
        }

        // 3. Check user has rights (via SecurityService)
        if (!securityService.isAdmin() && 
            !securityService.hasAccessToUserByExternalId(bikeRental.getRental().getUserExternalId())) {
            throw new UnauthorizedException("You don't have permission to upload photo for this bike rental");
        }

        // 4. Check only 1 photo exists (photoUrl should be null)
        if (bikeRental.getPhotoUrl() != null && !bikeRental.getPhotoUrl().isEmpty()) {
            throw new PhotoAlreadyExistsException(
                    "Photo already exists for this bike rental. Only one photo per rental is allowed."
            );
        }

        // 5. Validate photo (size, content-type)
        photoValidationService.validatePhoto(file);

        // 6. Upload to Azure Storage
        String photoUrl = azureBlobStorageService.uploadPhoto(file, bikeRentalId.toString());

        // 7. Save photoUrl to database
        bikeRental.setPhotoUrl(photoUrl);
        bikeRentalRepository.save(bikeRental);

        log.info("Successfully uploaded photo for bike rental ID: {}. Photo URL: {}", bikeRentalId, photoUrl);

        return PhotoUploadResponseDTO.builder()
                .photoUrl(photoUrl)
                .message("Photo uploaded successfully")
                .build();
    }

    /**
     * Check if rental status indicates the rental is ended.
     *
     * @param statusName the status name
     * @return true if rental is ended
     */
    private boolean isRentalEnded(String statusName) {
        if (statusName == null) {
            return false;
        }
        // Check for common "ended" status names (case-insensitive)
        String lowerStatus = statusName.toLowerCase();
        return lowerStatus.equals("Completed") ||
               lowerStatus.equals("Finished") ||
               lowerStatus.equals("Ended");
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
}
