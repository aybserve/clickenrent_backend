package org.clickenrent.paymentservice.validator;

import org.clickenrent.paymentservice.exception.InvalidIbanException;

import java.math.BigInteger;
import java.util.regex.Pattern;

/**
 * Validator for IBAN (International Bank Account Number) format
 * Implements ISO 13616 IBAN validation
 */
public class IbanValidator {

    private static final Pattern IBAN_PATTERN = Pattern.compile("^[A-Z]{2}[0-9]{2}[A-Z0-9]{1,30}$");
    
    /** Test IBANs used by payment providers (e.g. MultiSafepay) that do not pass mod-97 checksum. */
    private static final java.util.Set<String> TEST_IBANS = java.util.Set.of(
            "NL87ABNA0000000001", "NL87ABNA0000000002", "NL87ABNA0000000003", "NL87ABNA0000000004"
    );
    
    /**
     * Validate IBAN format and checksum
     * @param iban IBAN to validate
     * @return true if valid
     * @throws InvalidIbanException if invalid
     */
    public static boolean validate(String iban) {
        if (iban == null || iban.isEmpty()) {
            throw new InvalidIbanException(iban, "IBAN cannot be empty");
        }
        
        // Remove spaces and convert to uppercase
        String cleanIban = iban.replaceAll("\\s+", "").toUpperCase();
        
        // Check basic format
        if (!IBAN_PATTERN.matcher(cleanIban).matches()) {
            throw new InvalidIbanException(iban, "Invalid IBAN format. Must be 2 letters, 2 digits, followed by up to 30 alphanumeric characters");
        }
        
        // Check length based on country code
        if (!isValidLength(cleanIban)) {
            throw new InvalidIbanException(iban, "Invalid IBAN length for country code");
        }
        
        // Known test IBANs used by payment providers (e.g. MultiSafepay) - skip checksum
        if (TEST_IBANS.contains(cleanIban)) {
            return true;
        }
        
        // Validate checksum using mod-97 algorithm
        if (!isValidChecksum(cleanIban)) {
            throw new InvalidIbanException(iban, "Invalid IBAN checksum");
        }
        
        return true;
    }
    
    /**
     * Check if IBAN has valid length for its country
     */
    private static boolean isValidLength(String iban) {
        String countryCode = iban.substring(0, 2);
        int length = iban.length();
        
        // Common IBAN lengths by country
        return switch (countryCode) {
            case "AL" -> length == 28;
            case "AD" -> length == 24;
            case "AT" -> length == 20;
            case "BE" -> length == 16;
            case "BA" -> length == 20;
            case "BG" -> length == 22;
            case "HR" -> length == 21;
            case "CY" -> length == 28;
            case "CZ" -> length == 24;
            case "DK" -> length == 18;
            case "EE" -> length == 20;
            case "FI" -> length == 18;
            case "FR" -> length == 27;
            case "DE" -> length == 22;
            case "GI" -> length == 23;
            case "GR" -> length == 27;
            case "HU" -> length == 28;
            case "IS" -> length == 26;
            case "IE" -> length == 22;
            case "IT" -> length == 27;
            case "LV" -> length == 21;
            case "LI" -> length == 21;
            case "LT" -> length == 20;
            case "LU" -> length == 20;
            case "MT" -> length == 31;
            case "MC" -> length == 27;
            case "ME" -> length == 22;
            case "NL" -> length == 18;
            case "NO" -> length == 15;
            case "PL" -> length == 28;
            case "PT" -> length == 25;
            case "RO" -> length == 24;
            case "SM" -> length == 27;
            case "RS" -> length == 22;
            case "SK" -> length == 24;
            case "SI" -> length == 19;
            case "ES" -> length == 24;
            case "SE" -> length == 24;
            case "CH" -> length == 21;
            case "GB" -> length == 22;
            default -> true; // Allow unknown countries, checksum will validate
        };
    }
    
    /**
     * Validate IBAN checksum using mod-97 algorithm
     */
    private static boolean isValidChecksum(String iban) {
        // Move first 4 characters to end
        String rearranged = iban.substring(4) + iban.substring(0, 4);
        
        // Replace letters with numbers (A=10, B=11, ..., Z=35)
        StringBuilder numericIban = new StringBuilder();
        for (char c : rearranged.toCharArray()) {
            if (Character.isDigit(c)) {
                numericIban.append(c);
            } else {
                numericIban.append(Character.getNumericValue(c));
            }
        }
        
        // Calculate mod 97
        BigInteger ibanNumber = new BigInteger(numericIban.toString());
        return ibanNumber.mod(BigInteger.valueOf(97)).intValue() == 1;
    }
    
    /**
     * Format IBAN with spaces (every 4 characters)
     */
    public static String format(String iban) {
        if (iban == null) {
            return null;
        }
        String clean = iban.replaceAll("\\s+", "");
        return clean.replaceAll("(.{4})", "$1 ").trim();
    }
    
    /**
     * Clean IBAN by removing spaces and converting to uppercase
     */
    public static String clean(String iban) {
        if (iban == null) {
            return null;
        }
        return iban.replaceAll("\\s+", "").toUpperCase();
    }
}
