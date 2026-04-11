package com.taym.jet.assignment.exception;

public class InvalidPostalCodeException extends RuntimeException {
    public InvalidPostalCodeException(String postalCode){
        super("Invalid postcodde: " + postalCode);
    }
    
}
