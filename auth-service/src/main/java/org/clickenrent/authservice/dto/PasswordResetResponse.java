package org.clickenrent.authservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for password reset operations.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response for password reset")
public class PasswordResetResponse {

    @Schema(description = "Password reset successful", example = "true")
    private boolean success;

    @Schema(description = "Response message", example = "Password has been reset successfully")
    private String message;
}
