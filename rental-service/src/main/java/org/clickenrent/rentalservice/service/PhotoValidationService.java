package org.clickenrent.rentalservice.service;

import lombok.extern.slf4j.Slf4j;
import org.clickenrent.rentalservice.exception.PhotoValidationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

/**
 * Service for validating photo uploads.
 */
@Service
@Slf4j
public class PhotoValidationService {

    @Value("${photo.max-size-mb:5}")
    private int maxSizeMb;

    @Value("${photo.allowed-content-types:image/jpeg,image/png}")
    private String allowedContentTypesStr;

    /**
     * Validate photo file before upload.
     *
     * @param file the file to validate
     * @throws PhotoValidationException if validation fails
     */
    public void validatePhoto(MultipartFile file) {
        // Check if file is empty
        if (file == null || file.isEmpty()) {
            throw new PhotoValidationException("Photo file is required and cannot be empty");
        }

        // Check file size
        long maxSizeBytes = maxSizeMb * 1024L * 1024L;
        if (file.getSize() > maxSizeBytes) {
            throw new PhotoValidationException(
                    String.format("Photo file size exceeds maximum allowed size of %d MB. File size: %.2f MB",
                            maxSizeMb, file.getSize() / (1024.0 * 1024.0))
            );
        }

        // Check content type
        String contentType = file.getContentType();
        if (contentType == null || contentType.isEmpty()) {
            throw new PhotoValidationException("Photo content type is required");
        }

        List<String> allowedContentTypes = Arrays.asList(allowedContentTypesStr.split(","));
        if (!allowedContentTypes.contains(contentType)) {
            throw new PhotoValidationException(
                    String.format("Invalid photo content type: %s. Allowed types: %s",
                            contentType, String.join(", ", allowedContentTypes))
            );
        }

        log.debug("Photo validation passed. File: {}, Size: {} bytes, Content-Type: {}",
                file.getOriginalFilename(), file.getSize(), contentType);
    }
}


