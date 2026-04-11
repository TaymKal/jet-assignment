package com.taym.jet.assignment.exception;

public class RestaurantsNotFoundException extends RuntimeException {
    public RestaurantsNotFoundException(String postalCode){
        super("No restaurants found for postal code: " + postalCode);
    }
}
