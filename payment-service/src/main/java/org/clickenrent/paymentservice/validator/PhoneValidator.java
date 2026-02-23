package org.clickenrent.paymentservice.validator;

import org.clickenrent.paymentservice.exception.InvalidPhoneNumberException;

import java.util.regex.Pattern;

/**
 * Validator for international phone numbers
 * Supports E.164 format and common variations
 */
public class PhoneValidator {

    // E.164 format: + followed by 1-15 digits
    private static final Pattern E164_PATTERN = Pattern.compile("^\\+[1-9]\\d{1,14}$");
    
    // Relaxed pattern: optional +, followed by 7-15 digits (may include spaces, dashes, parentheses)
    private static final Pattern RELAXED_PATTERN = Pattern.compile("^\\+?[0-9\\s\\-\\(\\)]{7,20}$");
    
    /**
     * Validate phone number format
     * @param phone Phone number to validate
     * @return true if valid
     * @throws InvalidPhoneNumberException if invalid
     */
    public static boolean validate(String phone) {
        if (phone == null || phone.isEmpty()) {
            throw new InvalidPhoneNumberException(phone, "Phone number cannot be empty");
        }
        
        // Clean phone number (remove spaces, dashes, parentheses)
        String cleanPhone = clean(phone);
        
        // Ensure minimum/maximum digits (before other checks)
        String digitsOnly = cleanPhone.replaceAll("[^0-9]", "");
        if (digitsOnly.length() < 7) {
            throw new InvalidPhoneNumberException(phone, "Phone number must contain at least 7 digits");
        }
        if (digitsOnly.length() > 15) {
            throw new InvalidPhoneNumberException(phone, "Phone number cannot exceed 15 digits");
        }
        
        // Check if it matches E.164 format
        if (E164_PATTERN.matcher(cleanPhone).matches()) {
            return true;
        }
        
        // Check relaxed format (for numbers without country code)
        if (!RELAXED_PATTERN.matcher(phone).matches()) {
            throw new InvalidPhoneNumberException(phone, "Invalid phone number format. Expected format: +[country code][number] or [7-15 digits]");
        }
        
        return true;
    }
    
    /**
     * Validate phone number for specific country
     * @param phone Phone number
     * @param countryCode ISO country code (e.g., "NL", "BE", "ES")
     * @return true if valid
     */
    public static boolean validateForCountry(String phone, String countryCode) {
        validate(phone); // Basic validation first
        
        String cleanPhone = clean(phone);
        String digitsOnly = cleanPhone.replaceAll("[^0-9]", "");
        
        // Country-specific validation
        return switch (countryCode.toUpperCase()) {
            case "NL" -> validateDutchPhone(digitsOnly);
            case "BE" -> validateBelgianPhone(digitsOnly);
            case "ES" -> validateSpanishPhone(digitsOnly);
            case "PT" -> validatePortuguesePhone(digitsOnly);
            case "DE" -> validateGermanPhone(digitsOnly);
            case "FR" -> validateFrenchPhone(digitsOnly);
            default -> true; // Accept for unknown countries
        };
    }
    
    private static boolean validateDutchPhone(String digits) {
        // Dutch mobile: starts with 31 (country) + 6 (mobile prefix) + 8 digits
        // Or domestic: starts with 06 + 8 digits
        return (digits.startsWith("316") && digits.length() == 11) ||
               (digits.startsWith("06") && digits.length() == 10);
    }
    
    private static boolean validateBelgianPhone(String digits) {
        // Belgian mobile: starts with 32 (country) + 4XX + 6 digits
        // Or domestic: starts with 04XX + 6 digits
        return (digits.startsWith("324") && digits.length() == 11) ||
               (digits.startsWith("04") && digits.length() == 10);
    }
    
    private static boolean validateSpanishPhone(String digits) {
        // Spanish mobile: starts with 34 (country) + 6XX/7XX + 6 digits
        // Or domestic: starts with 6XX/7XX + 6 digits
        return (digits.startsWith("34") && (digits.startsWith("346") || digits.startsWith("347")) && digits.length() == 11) ||
               ((digits.startsWith("6") || digits.startsWith("7")) && digits.length() == 9);
    }
    
    private static boolean validatePortuguesePhone(String digits) {
        // Portuguese mobile: starts with 351 (country) + 9X + 7 digits
        // Or domestic: starts with 9X + 7 digits
        return (digits.startsWith("3519") && digits.length() == 12) ||
               (digits.startsWith("9") && digits.length() == 9);
    }
    
    private static boolean validateGermanPhone(String digits) {
        // German mobile: starts with 49 (country) + 1XX + 7-9 digits
        // Or domestic: starts with 01XX + 7-9 digits
        return (digits.startsWith("491") && digits.length() >= 11 && digits.length() <= 13) ||
               (digits.startsWith("01") && digits.length() >= 10 && digits.length() <= 12);
    }
    
    private static boolean validateFrenchPhone(String digits) {
        // French mobile: starts with 33 (country) + 6XX/7XX + 7 digits
        // Or domestic: starts with 06XX/07XX + 6 digits
        return (digits.startsWith("33") && (digits.startsWith("336") || digits.startsWith("337")) && digits.length() == 11) ||
               ((digits.startsWith("06") || digits.startsWith("07")) && digits.length() == 10);
    }
    
    /**
     * Clean phone number by removing formatting characters
     */
    public static String clean(String phone) {
        if (phone == null) {
            return null;
        }
        return phone.replaceAll("[\\s\\-\\(\\)\\.]", "");
    }
    
    /**
     * Format phone number to E.164 format if possible
     * @param phone Phone number
     * @param defaultCountryCode Country code to add if missing (e.g., "31" for NL)
     * @return Formatted phone number
     */
    public static String toE164(String phone, String defaultCountryCode) {
        if (phone == null) {
            return null;
        }
        
        String clean = clean(phone);
        
        // Already in E.164 format
        if (clean.startsWith("+")) {
            return clean;
        }
        
        // Add country code if missing
        if (!clean.startsWith("0")) {
            return "+" + defaultCountryCode + clean;
        }
        
        // Remove leading 0 and add country code
        return "+" + defaultCountryCode + clean.substring(1);
    }
}
