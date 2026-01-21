package org.clickenrent.authservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO representing the token response from Apple OAuth token endpoint.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppleTokenResponse {
    
    @JsonProperty("access_token")
    private String accessToken;
    
    @JsonProperty("expires_in")
    private Integer expiresIn;
    
    @JsonProperty("id_token")
    private String idToken;
    
    @JsonProperty("refresh_token")
    private String refreshToken;
    
    @JsonProperty("token_type")
    private String tokenType;
}
