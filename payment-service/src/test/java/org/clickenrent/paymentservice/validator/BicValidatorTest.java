package org.clickenrent.paymentservice.validator;

import org.clickenrent.paymentservice.exception.InvalidBicException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for BicValidator
 */
class BicValidatorTest {

    @ParameterizedTest
    @ValueSource(strings = {
        "ABNANL2A",  // ABN AMRO Netherlands - 8 chars
        "INGBNL2A",  // ING Netherlands - 8 chars
        "DEUTDEFF",  // Deutsche Bank Germany - 8 chars
        "NOLADE21KIE",  // Nordbank Germany - 11 chars
        "RZOOAT2L420",  // Raiffeisen Austria - 11 chars
        "BNPAFRPP",  // BNP Paribas France - 8 chars
        "BARCGB22",  // Barclays UK - 8 chars
        "CHASUS33"  // JP Morgan Chase US - 8 chars
    })
    void testValidBics(String bic) {
        assertDoesNotThrow(() -> BicValidator.validate(bic));
        assertTrue(BicValidator.validate(bic));
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "ABNA",  // Too short
        "ABNANL",  // Too short
        "ABNANL2",  // Too short (7 chars)
        "ABNANL2A123",  // Too long (12 chars)
        "12345678",  // All digits
        "ABNA1234",  // Invalid format (digits in bank code)
        "ABNANL12",  // Invalid country code (digits)
        "ABNA@@2A"  // Special characters
    })
    void testInvalidBics(String bic) {
        assertThrows(InvalidBicException.class, () -> BicValidator.validate(bic));
    }

    @Test
    void testNullBic() {
        assertThrows(InvalidBicException.class, () -> BicValidator.validate(null));
    }

    @Test
    void testEmptyBic() {
        assertThrows(InvalidBicException.class, () -> BicValidator.validate(""));
    }

    @Test
    void testBicWithSpaces() {
        String bicWithSpaces = "ABNA NL 2A";
        assertDoesNotThrow(() -> BicValidator.validate(bicWithSpaces));
        assertTrue(BicValidator.validate(bicWithSpaces));
    }

    @Test
    void testBicCleaning() {
        String bic = "ABNA NL 2A";
        String cleaned = BicValidator.clean(bic);
        assertEquals("ABNANL2A", cleaned);
    }

    @Test
    void testBicLowercaseConversion() {
        String bic = "abnanl2a";
        String cleaned = BicValidator.clean(bic);
        assertEquals("ABNANL2A", cleaned);
        assertDoesNotThrow(() -> BicValidator.validate(bic));
    }

    @Test
    void test8CharacterFormat() {
        assertTrue(BicValidator.is8CharacterFormat("ABNANL2A"));
        assertFalse(BicValidator.is8CharacterFormat("ABNANL2AXXX"));
        assertFalse(BicValidator.is8CharacterFormat("ABNA"));
    }

    @Test
    void test11CharacterFormat() {
        assertTrue(BicValidator.is11CharacterFormat("ABNANL2AXXX"));
        assertFalse(BicValidator.is11CharacterFormat("ABNANL2A"));
        assertFalse(BicValidator.is11CharacterFormat("ABNA"));
    }

    @Test
    void testMultiSafepayTestBics() {
        // Test BICs from MultiSafepay documentation
        assertDoesNotThrow(() -> BicValidator.validate("NOLADE22XXX"));  // Giropay
        assertDoesNotThrow(() -> BicValidator.validate("RZOOAT2L420"));  // EPS
    }

    @Test
    void testVariousCountryCodes() {
        assertDoesNotThrow(() -> BicValidator.validate("ABNANL2A"));  // NL - Netherlands
        assertDoesNotThrow(() -> BicValidator.validate("DEUTDEFF"));  // DE - Germany
        assertDoesNotThrow(() -> BicValidator.validate("BNPAFRPP"));  // FR - France
        assertDoesNotThrow(() -> BicValidator.validate("BARCGB22"));  // GB - UK
        assertDoesNotThrow(() -> BicValidator.validate("UNCRITMM"));  // IT - Italy
        assertDoesNotThrow(() -> BicValidator.validate("CAIXESBB"));  // ES - Spain
        assertDoesNotThrow(() -> BicValidator.validate("GEBABEBB"));  // BE - Belgium
        assertDoesNotThrow(() -> BicValidator.validate("RZBAATWW"));  // AT - Austria
    }
}
