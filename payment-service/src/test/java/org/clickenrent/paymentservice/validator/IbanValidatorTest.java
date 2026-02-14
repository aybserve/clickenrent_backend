package org.clickenrent.paymentservice.validator;

import org.clickenrent.paymentservice.exception.InvalidIbanException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for IbanValidator
 */
class IbanValidatorTest {

    @ParameterizedTest
    @ValueSource(strings = {
        "NL91ABNA0417164300",  // Netherlands
        "DE89370400440532013000",  // Germany
        "GB29NWBK60161331926819",  // United Kingdom
        "FR1420041010050500013M02606",  // France
        "IT60X0542811101000000123456",  // Italy
        "ES9121000418450200051332",  // Spain
        "BE68539007547034",  // Belgium
        "AT611904300234573201",  // Austria
        "CH9300762011623852957",  // Switzerland
        "NL87ABNA0000000001"  // MultiSafepay test IBAN
    })
    void testValidIbans(String iban) {
        assertDoesNotThrow(() -> IbanValidator.validate(iban));
        assertTrue(IbanValidator.validate(iban));
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "NL91ABNA041716430",  // Too short
        "NL91ABNA04171643000",  // Too long
        "XX91ABNA0417164300",  // Invalid country code format
        "NL00ABNA0417164300",  // Invalid checksum
        "1234567890123456"  // No country code
    })
    void testInvalidIbans(String iban) {
        assertThrows(InvalidIbanException.class, () -> IbanValidator.validate(iban));
    }

    @Test
    void testNullIban() {
        assertThrows(InvalidIbanException.class, () -> IbanValidator.validate(null));
    }

    @Test
    void testEmptyIban() {
        assertThrows(InvalidIbanException.class, () -> IbanValidator.validate(""));
    }

    @Test
    void testIbanWithSpaces() {
        String ibanWithSpaces = "NL91 ABNA 0417 1643 00";
        assertDoesNotThrow(() -> IbanValidator.validate(ibanWithSpaces));
        assertTrue(IbanValidator.validate(ibanWithSpaces));
    }

    @Test
    void testIbanCleaning() {
        String iban = "NL91 ABNA 0417 1643 00";
        String cleaned = IbanValidator.clean(iban);
        assertEquals("NL91ABNA0417164300", cleaned);
    }

    @Test
    void testIbanFormatting() {
        String iban = "NL91ABNA0417164300";
        String formatted = IbanValidator.format(iban);
        assertEquals("NL91 ABNA 0417 1643 00", formatted);
    }

    @Test
    void testMultiSafepayTestIbans() {
        // Test IBANs from MultiSafepay documentation
        assertDoesNotThrow(() -> IbanValidator.validate("NL87ABNA0000000001"));
        assertDoesNotThrow(() -> IbanValidator.validate("NL87ABNA0000000002"));
        assertDoesNotThrow(() -> IbanValidator.validate("NL87ABNA0000000003"));
        assertDoesNotThrow(() -> IbanValidator.validate("NL87ABNA0000000004"));
    }

    @Test
    void testCountrySpecificLengths() {
        // Test various country IBAN lengths
        assertDoesNotThrow(() -> IbanValidator.validate("BE68539007547034")); // Belgium - 16 chars
        assertDoesNotThrow(() -> IbanValidator.validate("NL91ABNA0417164300")); // Netherlands - 18 chars
        assertDoesNotThrow(() -> IbanValidator.validate("AT611904300234573201")); // Austria - 20 chars
        assertDoesNotThrow(() -> IbanValidator.validate("DE89370400440532013000")); // Germany - 22 chars
    }
}
