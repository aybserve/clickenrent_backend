package org.clickenrent.paymentservice.validator;

import org.clickenrent.paymentservice.exception.InvalidPhoneNumberException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for PhoneValidator
 */
class PhoneValidatorTest {

    @ParameterizedTest
    @ValueSource(strings = {
        "+31612345678",  // Netherlands mobile (iDEAL)
        "+34612345678",  // Spain mobile (Bizum)
        "+351912345678",  // Portugal mobile (MB WAY)
        "+4915123456789",  // Germany mobile
        "+32461234567",  // Belgium mobile
        "+33612345678",  // France mobile
        "+14155551234",  // US mobile
        "+442071234567"  // UK mobile
    })
    void testValidE164Phones(String phone) {
        assertDoesNotThrow(() -> PhoneValidator.validate(phone));
        assertTrue(PhoneValidator.validate(phone));
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "0612345678",  // Netherlands domestic
        "612345678",  // Spain domestic
        "912345678",  // Portugal domestic
        "015123456789"  // Germany domestic
    })
    void testValidDomesticPhones(String phone) {
        // These should pass basic validation (relaxed pattern)
        assertDoesNotThrow(() -> PhoneValidator.validate(phone));
    }

    @Test
    void testPhoneWithSpacesDashesParentheses() {
        assertDoesNotThrow(() -> PhoneValidator.validate("+31 6 12 34 56 78"));
        assertDoesNotThrow(() -> PhoneValidator.validate("+31-6-12345678"));
        assertDoesNotThrow(() -> PhoneValidator.validate("(+31) 612345678"));
    }

    @Test
    void testNullPhone() {
        assertThrows(InvalidPhoneNumberException.class, () -> PhoneValidator.validate(null));
    }

    @Test
    void testEmptyPhone() {
        assertThrows(InvalidPhoneNumberException.class, () -> PhoneValidator.validate(""));
    }

    @Test
    void testTooShortPhone() {
        assertThrows(InvalidPhoneNumberException.class, () -> PhoneValidator.validate("123456"));
        assertThrows(InvalidPhoneNumberException.class, () -> PhoneValidator.validate("+31123"));
    }

    @Test
    void testTooLongPhone() {
        assertThrows(InvalidPhoneNumberException.class, () -> 
            PhoneValidator.validate("+123456789012345678"));
    }

    @Test
    void testPhoneCleaning() {
        assertEquals("+31612345678", PhoneValidator.clean("+31 6 12 34 56 78"));
        assertEquals("+31612345678", PhoneValidator.clean("+31-6-12345678"));
        assertEquals("+31612345678", PhoneValidator.clean("(+31) 612345678"));
    }

    @ParameterizedTest
    @CsvSource({
        "+31612345678, NL, true",
        "0612345678, NL, true",
        "+31512345678, NL, false",  // Not mobile prefix
        "+34612345678, ES, true",
        "+34512345678, ES, false",  // Not mobile prefix
        "+351912345678, PT, true",
        "+351812345678, PT, false"  // Not mobile prefix
    })
    void testCountrySpecificValidation(String phone, String country, boolean shouldPass) {
        if (shouldPass) {
            assertDoesNotThrow(() -> PhoneValidator.validateForCountry(phone, country));
        } else {
            assertFalse(PhoneValidator.validateForCountry(phone, country));
        }
    }

    @Test
    void testE164Conversion() {
        // Already in E.164
        assertEquals("+31612345678", PhoneValidator.toE164("+31612345678", "31"));
        
        // Domestic with leading 0
        assertEquals("+31612345678", PhoneValidator.toE164("0612345678", "31"));
        
        // Without country code
        assertEquals("+31612345678", PhoneValidator.toE164("612345678", "31"));
    }

    @Test
    void testMultiSafepayTestPhones() {
        // Test phones from MultiSafepay documentation
        assertDoesNotThrow(() -> PhoneValidator.validate("+34612345678"));  // Bizum
        assertDoesNotThrow(() -> PhoneValidator.validate("+351912345678"));  // MB WAY
        assertDoesNotThrow(() -> PhoneValidator.validate("+31612345678"));  // Netherlands
    }

    @Test
    void testSpanishPhoneForBizum() {
        // Bizum requires Spanish mobile
        assertTrue(PhoneValidator.validateForCountry("+34612345678", "ES"));
        assertTrue(PhoneValidator.validateForCountry("+34712345678", "ES"));
        
        // Invalid for Bizum (not mobile or not Spanish)
        assertFalse(PhoneValidator.validateForCountry("+34912345678", "ES")); // Landline
    }

    @Test
    void testPortuguesePhoneForMBWay() {
        // MB WAY requires Portuguese mobile
        assertTrue(PhoneValidator.validateForCountry("+351912345678", "PT"));
        assertTrue(PhoneValidator.validateForCountry("+351961234567", "PT"));
        
        // Invalid for MB WAY (not mobile)
        assertFalse(PhoneValidator.validateForCountry("+351212345678", "PT")); // Landline
    }
}
