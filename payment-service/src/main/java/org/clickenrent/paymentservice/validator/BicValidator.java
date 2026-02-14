package org.clickenrent.paymentservice.validator;

import org.clickenrent.paymentservice.exception.InvalidBicException;

import java.util.regex.Pattern;

/**
 * Validator for BIC/SWIFT codes
 * Implements ISO 9362 BIC validation
 */
public class BicValidator {

    // BIC format: 4 letters (bank code) + 2 letters (country) + 2 alphanumeric (location) + optional 3 alphanumeric (branch)
    private static final Pattern BIC_PATTERN_8 = Pattern.compile("^[A-Z]{6}[A-Z0-9]{2}$");
    private static final Pattern BIC_PATTERN_11 = Pattern.compile("^[A-Z]{6}[A-Z0-9]{5}$");
    
    /**
     * Validate BIC/SWIFT code format
     * @param bic BIC code to validate
     * @return true if valid
     * @throws InvalidBicException if invalid
     */
    public static boolean validate(String bic) {
        if (bic == null || bic.isEmpty()) {
            throw new InvalidBicException(bic, "BIC cannot be empty");
        }
        
        // Remove spaces and convert to uppercase
        String cleanBic = bic.replaceAll("\\s+", "").toUpperCase();
        
        // BIC must be 8 or 11 characters
        if (cleanBic.length() != 8 && cleanBic.length() != 11) {
            throw new InvalidBicException(bic, "BIC must be 8 or 11 characters long");
        }
        
        // Validate format
        boolean isValid = cleanBic.length() == 8 
            ? BIC_PATTERN_8.matcher(cleanBic).matches()
            : BIC_PATTERN_11.matcher(cleanBic).matches();
        
        if (!isValid) {
            throw new InvalidBicException(bic, "Invalid BIC format. Must be: 4 letters (bank) + 2 letters (country) + 2 alphanumeric (location) + optional 3 alphanumeric (branch)");
        }
        
        // Validate country code (ISO 3166-1 alpha-2)
        String countryCode = cleanBic.substring(4, 6);
        if (!isValidCountryCode(countryCode)) {
            throw new InvalidBicException(bic, "Invalid country code: " + countryCode);
        }
        
        return true;
    }
    
    /**
     * Check if country code is valid (simplified check for common countries)
     */
    private static boolean isValidCountryCode(String countryCode) {
        // List of valid ISO 3166-1 alpha-2 country codes (European countries + major others)
        return switch (countryCode) {
            case "AT", "BE", "BG", "HR", "CY", "CZ", "DK", "EE", "FI", "FR",
                 "DE", "GR", "HU", "IE", "IT", "LV", "LT", "LU", "MT", "NL",
                 "PL", "PT", "RO", "SK", "SI", "ES", "SE", "GB", "CH", "NO",
                 "IS", "LI", "US", "CA", "AU", "NZ", "JP", "CN", "IN", "BR",
                 "RU", "ZA", "MX", "SG", "HK", "AE", "SA" -> true;
            default -> false;
        };
    }
    
    /**
     * Clean BIC by removing spaces and converting to uppercase
     */
    public static String clean(String bic) {
        if (bic == null) {
            return null;
        }
        return bic.replaceAll("\\s+", "").toUpperCase();
    }
    
    /**
     * Check if BIC is in 8-character format (without branch code)
     */
    public static boolean is8CharacterFormat(String bic) {
        if (bic == null) {
            return false;
        }
        return clean(bic).length() == 8;
    }
    
    /**
     * Check if BIC is in 11-character format (with branch code)
     */
    public static boolean is11CharacterFormat(String bic) {
        if (bic == null) {
            return false;
        }
        return clean(bic).length() == 11;
    }
}
