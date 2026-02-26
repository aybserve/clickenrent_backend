package org.clickenrent.paymentservice.validator;

import org.clickenrent.paymentservice.exception.InvalidCardException;

import java.util.regex.Pattern;

/**
 * Validator for credit/debit card numbers
 * Implements Luhn algorithm (MOD 10) validation
 */
public class CardNumberValidator {

    private static final Pattern CARD_NUMBER_PATTERN = Pattern.compile("^[0-9]{13,19}$");
    
    /**
     * Validate card number using Luhn algorithm
     * @param cardNumber Card number to validate
     * @return true if valid
     * @throws InvalidCardException if invalid
     */
    public static boolean validate(String cardNumber) {
        if (cardNumber == null || cardNumber.isEmpty()) {
            throw new InvalidCardException("number", "Card number cannot be empty");
        }
        
        // Remove spaces and dashes
        String cleanNumber = cardNumber.replaceAll("[\\s\\-]", "");
        
        // Check format (13-19 digits)
        if (!CARD_NUMBER_PATTERN.matcher(cleanNumber).matches()) {
            throw new InvalidCardException("number", "Card number must be 13-19 digits");
        }
        
        // Validate using Luhn algorithm
        if (!luhnCheck(cleanNumber)) {
            throw new InvalidCardException("number", "Invalid card number (failed Luhn check)");
        }
        
        return true;
    }
    
    /**
     * Luhn algorithm (MOD 10) checksum validation
     */
    private static boolean luhnCheck(String cardNumber) {
        int sum = 0;
        boolean alternate = false;
        
        // Process digits from right to left
        for (int i = cardNumber.length() - 1; i >= 0; i--) {
            int digit = Character.getNumericValue(cardNumber.charAt(i));
            
            if (alternate) {
                digit *= 2;
                if (digit > 9) {
                    digit -= 9;
                }
            }
            
            sum += digit;
            alternate = !alternate;
        }
        
        return (sum % 10 == 0);
    }
    
    /**
     * Detect card type from card number
     */
    public static String detectCardType(String cardNumber) {
        if (cardNumber == null) {
            return "UNKNOWN";
        }
        
        String clean = cardNumber.replaceAll("[\\s\\-]", "");
        
        // Visa: starts with 4
        if (clean.matches("^4[0-9]{12,18}$")) {
            return "VISA";
        }
        
        // Mastercard: starts with 51-55 or 2221-2720
        if (clean.matches("^5[1-5][0-9]{14}$") || 
            clean.matches("^2(22[1-9]|2[3-9][0-9]|[3-6][0-9]{2}|7[0-1][0-9]|720)[0-9]{12}$")) {
            return "MASTERCARD";
        }
        
        // American Express: starts with 34 or 37
        if (clean.matches("^3[47][0-9]{13}$")) {
            return "AMEX";
        }
        
        // Maestro: starts with 5018, 5020, 5038, 5893, 6304, 6703, 6759, 6761, 6762, 6763
        if (clean.matches("^(5018|5020|5038|5893|6304|6703|6759|6761|6762|6763)[0-9]{8,15}$")) {
            return "MAESTRO";
        }
        
        // Discover: starts with 6011, 622126-622925, 644-649, 65
        if (clean.matches("^6(011|5[0-9]{2}|4[4-9][0-9]|22(12[6-9]|1[3-9][0-9]|[2-8][0-9]{2}|9[01][0-9]|92[0-5]))[0-9]{10,13}$")) {
            return "DISCOVER";
        }
        
        // Diners Club: starts with 36, 38, or 300-305
        if (clean.matches("^3(0[0-5]|[68][0-9])[0-9]{11,14}$")) {
            return "DINERS";
        }
        
        // JCB: starts with 3528-3589
        if (clean.matches("^35(2[8-9]|[3-8][0-9])[0-9]{12,15}$")) {
            return "JCB";
        }
        
        return "UNKNOWN";
    }
    
    /**
     * Clean card number by removing spaces and dashes
     */
    public static String clean(String cardNumber) {
        if (cardNumber == null) {
            return null;
        }
        return cardNumber.replaceAll("[\\s\\-]", "");
    }
    
    /**
     * Mask card number for display (show only last 4 digits)
     */
    public static String mask(String cardNumber) {
        if (cardNumber == null) {
            return null;
        }
        
        String clean = clean(cardNumber);
        if (clean.length() < 4) {
            return "****";
        }
        
        String last4 = clean.substring(clean.length() - 4);
        return "**** **** **** " + last4;
    }
    
    /**
     * Format card number with spaces (every 4 digits)
     */
    public static String format(String cardNumber) {
        if (cardNumber == null) {
            return null;
        }
        
        String clean = clean(cardNumber);
        
        // American Express format: 4-6-5
        if (detectCardType(clean).equals("AMEX")) {
            if (clean.length() >= 15) {
                return clean.substring(0, 4) + " " + 
                       clean.substring(4, 10) + " " + 
                       clean.substring(10);
            }
        }
        
        // Standard format: 4-4-4-4
        return clean.replaceAll("(.{4})", "$1 ").trim();
    }
    
    /**
     * Validate card expiry date
     * @param month Month (1-12)
     * @param year Year (4 digits or 2 digits)
     * @return true if valid and not expired
     */
    public static boolean validateExpiry(int month, int year) {
        if (month < 1 || month > 12) {
            throw new InvalidCardException("expiry", "Invalid month (must be 1-12)");
        }
        
        // Convert 2-digit year to 4-digit
        if (year < 100) {
            year += 2000;
        }
        
        // Get current date
        java.time.YearMonth now = java.time.YearMonth.now();
        java.time.YearMonth expiry = java.time.YearMonth.of(year, month);
        
        if (expiry.isBefore(now)) {
            throw new InvalidCardException("expiry", "Card has expired");
        }
        
        return true;
    }
    
    /**
     * Validate CVV/CVC code
     * @param cvv CVV code
     * @param cardType Card type (for determining expected length)
     * @return true if valid
     */
    public static boolean validateCVV(String cvv, String cardType) {
        if (cvv == null || cvv.isEmpty()) {
            throw new InvalidCardException("CVV", "CVV cannot be empty");
        }
        
        if (!cvv.matches("^[0-9]{3,4}$")) {
            throw new InvalidCardException("CVV", "CVV must be 3 or 4 digits");
        }
        
        // American Express uses 4-digit CVV
        if ("AMEX".equals(cardType) && cvv.length() != 4) {
            throw new InvalidCardException("CVV", "American Express CVV must be 4 digits");
        }
        
        // Other cards use 3-digit CVV
        if (!"AMEX".equals(cardType) && cvv.length() != 3) {
            throw new InvalidCardException("CVV", "CVV must be 3 digits");
        }
        
        return true;
    }
}
