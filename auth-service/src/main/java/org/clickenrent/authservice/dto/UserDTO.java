package org.clickenrent.authservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for User entity.
 * Contains user profile information and audit fields.
 * Password is excluded for security reasons.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;
    
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Pattern(regexp = "^[a-zA-Z0-9-]{1,100}$", message = "Invalid external ID format")
    private String externalId;
    
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String userName;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    private String email;
    
    @NotBlank(message = "First name is required")
    @Size(min = 1, max = 50, message = "First name must be between 1 and 50 characters")
    private String firstName;
    
    @NotBlank(message = "Last name is required")
    @Size(min = 1, max = 50, message = "Last name must be between 1 and 50 characters")
    private String lastName;
    
    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Invalid phone number format")
    @Size(max = 20, message = "Phone must not exceed 20 characters")
    private String phone;
    
    @Size(max = 500, message = "Image URL must not exceed 500 characters")
    private String imageUrl;
    
    private Long languageId;
    private String languageCode; // For analytics (external_id from Language entity)
    private Boolean isActive;
    private Boolean isDeleted;
    private Boolean isEmailVerified;
    private Boolean isAcceptedTerms;
    private Boolean isAcceptedPrivacyPolicy;

    // Audit fields
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime dateCreated;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime lastDateModified;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String createdBy;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String lastModifiedBy;
}


