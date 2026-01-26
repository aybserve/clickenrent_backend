package org.clickenrent.authservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Response DTO for password reset token validation.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Token validation response")
public class TokenValidationResponse {

    @Schema(description = "Whether token is valid", example = "true")
    private boolean valid;

    @Schema(description = "Validation message", example = "Token is valid")
    private String message;

    @Schema(description = "Token expiration time", example = "2026-01-26T15:30:00")
    private LocalDateTime expiresAt;

    @Schema(description = "Remaining attempts", example = "2")
    private Integer remainingAttempts;
}
