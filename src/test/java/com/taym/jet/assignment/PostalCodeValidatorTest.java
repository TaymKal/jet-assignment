package com.taym.jet.assignment;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.taym.jet.assignment.validator.PostalCodeValidator;

public class PostalCodeValidatorTest {
    private final PostalCodeValidator validator = new PostalCodeValidator();

    @Test
    void validUkPostalCodes(){


        // Standard
        assertTrue(validator.isValidUkPostalCode("EC4M7RF"));

        // Lowercase
        assertTrue(validator.isValidUkPostalCode("sw1a1aa"));

        // Exception
        assertTrue(validator.isValidUkPostalCode("GIR0AA"));

        // By format:

        // AANA NAA
        assertTrue(validator.isValidUkPostalCode("EC4M 7RF"));
        // AN NAA
        assertTrue(validator.isValidUkPostalCode("M1 1AA"));
        // ANN NAA
        assertTrue(validator.isValidUkPostalCode("M60 1NW"));
        // AAN NAA 
        assertTrue(validator.isValidUkPostalCode("CR2 6XH"));
        // AANN NAA 
        assertTrue(validator.isValidUkPostalCode("DN55 1PT"));
        // ANA NAA
        assertTrue(validator.isValidUkPostalCode("W1A 1HQ"));

    }

    @Test
    void invalidUkPostalCodes(){


        assertFalse(validator.isValidUkPostalCode(null));
        assertFalse(validator.isValidUkPostalCode(""));
        assertFalse(validator.isValidUkPostalCode(" "));

        // Numbers only
        assertFalse(validator.isValidUkPostalCode("12530")); 

        // Excessive length
        assertFalse(validator.isValidUkPostalCode("WC1A 1AA B"));
        
        // Special characters
        assertFalse(validator.isValidUkPostalCode("EC4M-7RF"));

        // Disallowed first-letter rules
        assertFalse(validator.isValidUkPostalCode("QV1A1AA"));
        assertFalse(validator.isValidUkPostalCode("V1 1AA"));
        assertFalse(validator.isValidUkPostalCode("X1 1AA"));

        // Unsafe path-like input
        assertFalse(validator.isValidUkPostalCode("EC4M7RF/../../"));

        // Tabs/newlines in between should still normalize as spaces and validate
        assertTrue(validator.isValidUkPostalCode("EC4M\t7RF"));
        assertTrue(validator.isValidUkPostalCode("EC4M\n7RF"));

    }
}
