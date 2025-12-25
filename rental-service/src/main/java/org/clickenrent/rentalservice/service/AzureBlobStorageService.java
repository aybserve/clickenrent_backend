package org.clickenrent.rentalservice.service;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * Service for managing Azure Blob Storage operations.
 */
@Service
@Slf4j
public class AzureBlobStorageService {

    @Value("${azure.storage.connection-string}")
    private String connectionString;

    @Value("${azure.storage.container-name}")
    private String containerName;

    private BlobServiceClient blobServiceClient;
    private BlobContainerClient containerClient;

    /**
     * Initialize Azure Blob Storage clients.
     */
    private void initializeClients() {
        if (blobServiceClient == null) {
            blobServiceClient = new BlobServiceClientBuilder()
                    .connectionString(connectionString)
                    .buildClient();
            containerClient = blobServiceClient.getBlobContainerClient(containerName);
            
            // Create container if it doesn't exist
            if (!containerClient.exists()) {
                containerClient.create();
                log.info("Created Azure Blob Storage container: {}", containerName);
            }
        }
    }

    /**
     * Upload photo to Azure Blob Storage.
     *
     * @param file the photo file to upload
     * @param bikeRentalId the bike rental ID
     * @return the URL of the uploaded photo
     * @throws RuntimeException if upload fails
     */
    public String uploadPhoto(MultipartFile file, String bikeRentalId) {
        try {
            initializeClients();

            String blobName = generateUniqueBlobName(file.getOriginalFilename(), bikeRentalId);
            BlobClient blobClient = containerClient.getBlobClient(blobName);

            // Upload the file
            try (InputStream inputStream = file.getInputStream()) {
                blobClient.upload(inputStream, file.getSize(), true);
            }

            String photoUrl = blobClient.getBlobUrl();
            log.info("Successfully uploaded photo to Azure Blob Storage. URL: {}", photoUrl);
            
            return photoUrl;
        } catch (IOException e) {
            log.error("Failed to upload photo to Azure Blob Storage", e);
            throw new RuntimeException("Failed to upload photo: " + e.getMessage(), e);
        }
    }

    /**
     * Delete photo from Azure Blob Storage.
     *
     * @param photoUrl the URL of the photo to delete
     */
    public void deletePhoto(String photoUrl) {
        try {
            initializeClients();

            // Extract blob name from URL
            String blobName = extractBlobNameFromUrl(photoUrl);
            if (blobName == null) {
                log.warn("Could not extract blob name from URL: {}", photoUrl);
                return;
            }

            BlobClient blobClient = containerClient.getBlobClient(blobName);
            if (blobClient.exists()) {
                blobClient.delete();
                log.info("Successfully deleted photo from Azure Blob Storage: {}", blobName);
            } else {
                log.warn("Photo not found in Azure Blob Storage: {}", blobName);
            }
        } catch (Exception e) {
            log.error("Failed to delete photo from Azure Blob Storage: {}", photoUrl, e);
            // Don't throw exception for delete failures
        }
    }

    /**
     * Generate a unique blob name for the photo.
     *
     * @param originalFilename the original filename
     * @param bikeRentalId the bike rental ID
     * @return unique blob name
     */
    public String generateUniqueBlobName(String originalFilename, String bikeRentalId) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String uniqueId = UUID.randomUUID().toString().substring(0, 8);
        String extension = getFileExtension(originalFilename);
        
        return String.format("bike-rental-%s_%s_%s%s", 
                bikeRentalId, timestamp, uniqueId, extension);
    }

    /**
     * Extract file extension from filename.
     *
     * @param filename the filename
     * @return file extension with dot (e.g., ".jpg")
     */
    private String getFileExtension(String filename) {
        if (filename == null || filename.isEmpty()) {
            return "";
        }
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex > 0 && lastDotIndex < filename.length() - 1) {
            return filename.substring(lastDotIndex);
        }
        return "";
    }

    /**
     * Extract blob name from full URL.
     *
     * @param url the full blob URL
     * @return blob name or null if extraction fails
     */
    private String extractBlobNameFromUrl(String url) {
        if (url == null || url.isEmpty()) {
            return null;
        }
        
        try {
            // URL format: https://<account>.blob.core.windows.net/<container>/<blobname>
            String[] parts = url.split("/" + containerName + "/");
            if (parts.length == 2) {
                return parts[1];
            }
        } catch (Exception e) {
            log.error("Error extracting blob name from URL: {}", url, e);
        }
        
        return null;
    }
}

