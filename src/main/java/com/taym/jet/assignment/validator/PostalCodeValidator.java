package com.taym.jet.assignment.validator;

import org.springframework.stereotype.Component;

@Component
public class PostalCodeValidator {
    public boolean isValidUkPostalCode(String postalCode){
        if (postalCode == null) return false;
        String normalized = postalCode.trim().replaceAll("\\s+", "").toUpperCase();
        return normalized.matches("^(GIR0AA|[A-Z]{1,2}[0-9][0-9A-Z]?[0-9][A-Z]{2})$");
    }
}
