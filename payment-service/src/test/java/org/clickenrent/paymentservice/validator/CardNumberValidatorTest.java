package org.clickenrent.paymentservice.validator;

import org.clickenrent.paymentservice.exception.InvalidCardException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for CardNumberValidator
 */
class CardNumberValidatorTest {

    @ParameterizedTest
    @ValueSource(strings = {
        "4111111111111111",  // Visa test card
        "4761340000000019",  // Visa test card
        "5500000000000004",  // Mastercard test card
        "374500000000015",  // Amex test card
        "6799990000000000011"  // Maestro test card
    })
    void testValidMultiSafepayTestCards(String cardNumber) {
        assertDoesNotThrow(() -> CardNumberValidator.validate(cardNumber));
        assertTrue(CardNumberValidator.validate(cardNumber));
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "67034500054620008",  // Bancontact test card (completed)
        "67039902990000045",  // Bancontact test card (declined 3D)
        "67039902990000011"  // Bancontact test card (declined funds)
    })
    void testBancontactTestCards(String cardNumber) {
        assertDoesNotThrow(() -> CardNumberValidator.validate(cardNumber));
        assertTrue(CardNumberValidator.validate(cardNumber));
    }

    @ParameterizedTest
    @CsvSource({
        "4111111111111111, VISA",
        "5500000000000004, MASTERCARD",
        "374500000000015, AMEX",
        "6799990000000000011, MAESTRO",
        "4761340000000019, VISA",
        "67034500054620008, MAESTRO"
    })
    void testCardTypeDetection(String cardNumber, String expectedType) {
        String detectedType = CardNumberValidator.detectCardType(cardNumber);
        assertEquals(expectedType, detectedType);
    }

    @Test
    void testInvalidCardNumber() {
        assertThrows(InvalidCardException.class, () -> 
            CardNumberValidator.validate("4111111111111112")); // Invalid Luhn
        assertThrows(InvalidCardException.class, () -> 
            CardNumberValidator.validate("123")); // Too short
        assertThrows(InvalidCardException.class, () -> 
            CardNumberValidator.validate("12345678901234567890")); // Too long
    }

    @Test
    void testNullCardNumber() {
        assertThrows(InvalidCardException.class, () -> CardNumberValidator.validate(null));
    }

    @Test
    void testEmptyCardNumber() {
        assertThrows(InvalidCardException.class, () -> CardNumberValidator.validate(""));
    }

    @Test
    void testCardNumberWithSpaces() {
        String cardWithSpaces = "4111 1111 1111 1111";
        assertDoesNotThrow(() -> CardNumberValidator.validate(cardWithSpaces));
        assertTrue(CardNumberValidator.validate(cardWithSpaces));
    }

    @Test
    void testCardNumberWithDashes() {
        String cardWithDashes = "4111-1111-1111-1111";
        assertDoesNotThrow(() -> CardNumberValidator.validate(cardWithDashes));
        assertTrue(CardNumberValidator.validate(cardWithDashes));
    }

    @Test
    void testCardNumberCleaning() {
        String card = "4111 1111 1111 1111";
        String cleaned = CardNumberValidator.clean(card);
        assertEquals("4111111111111111", cleaned);
    }

    @Test
    void testCardNumberMasking() {
        String card = "4111111111111111";
        String masked = CardNumberValidator.mask(card);
        assertEquals("**** **** **** 1111", masked);
    }

    @Test
    void testCardNumberFormatting() {
        String card = "4111111111111111";
        String formatted = CardNumberValidator.format(card);
        assertEquals("4111 1111 1111 1111", formatted);
    }

    @Test
    void testAmexFormatting() {
        String amexCard = "374500000000015";
        String formatted = CardNumberValidator.format(amexCard);
        assertEquals("3745 000000 00015", formatted);
    }

    @ParameterizedTest
    @CsvSource({
        "123, VISA, true",
        "1234, AMEX, true",
        "12, VISA, false",
        "123, AMEX, false",
        "12345, VISA, false"
    })
    void testCvvValidation(String cvv, String cardType, boolean shouldPass) {
        if (shouldPass) {
            assertDoesNotThrow(() -> CardNumberValidator.validateCVV(cvv, cardType));
        } else {
            assertThrows(InvalidCardException.class, () -> 
                CardNumberValidator.validateCVV(cvv, cardType));
        }
    }

    @Test
    void testExpiryValidation() {
        // Valid future dates
        assertDoesNotThrow(() -> CardNumberValidator.validateExpiry(12, 2025));
        assertDoesNotThrow(() -> CardNumberValidator.validateExpiry(12, 2030));
        assertDoesNotThrow(() -> CardNumberValidator.validateExpiry(1, 2027));
        
        // Invalid: expired
        assertThrows(InvalidCardException.class, () -> 
            CardNumberValidator.validateExpiry(1, 2020));
        
        // Invalid: month out of range
        assertThrows(InvalidCardException.class, () -> 
            CardNumberValidator.validateExpiry(13, 2025));
        assertThrows(InvalidCardException.class, () -> 
            CardNumberValidator.validateExpiry(0, 2025));
    }

    @Test
    void testLuhnAlgorithm() {
        // Valid Luhn checksums
        assertTrue(CardNumberValidator.validate("4111111111111111"));
        assertTrue(CardNumberValidator.validate("5500000000000004"));
        assertTrue(CardNumberValidator.validate("374500000000015"));
        
        // Invalid Luhn checksums
        assertThrows(InvalidCardException.class, () -> 
            CardNumberValidator.validate("4111111111111112"));
        assertThrows(InvalidCardException.class, () -> 
            CardNumberValidator.validate("5500000000000005"));
    }
}
