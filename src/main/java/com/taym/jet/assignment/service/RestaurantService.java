package com.taym.jet.assignment.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import com.taym.jet.assignment.exception.InvalidPostalCodeException;
import com.taym.jet.assignment.exception.RestaurantsNotFoundException;
import com.taym.jet.assignment.exception.UpstreamServiceException;
import com.taym.jet.assignment.model.RestaurantResponse;
import com.taym.jet.assignment.validator.PostalCodeValidator;

@Service
public class RestaurantService {
    private RestClient restClient;
    private PostalCodeValidator postalCodeValidator;
    
    public RestaurantService(RestClient restClient, PostalCodeValidator postalCodeValidator){
        this.restClient = restClient;
        this.postalCodeValidator = postalCodeValidator;
    }

    public RestaurantResponse getUkRestaurants(String postalCode){
        
        if (postalCode == null || postalCode.isBlank() || !postalCodeValidator.isValidUkPostalCode(postalCode)) {
            throw new InvalidPostalCodeException("Postal code must be valid");
        }

        try {
            RestaurantResponse response = restClient.get()
                    .uri("/discovery/uk/restaurants/enriched/bypostcode/{postcode}", postalCode)
                    .retrieve()
                    .onStatus(
                            status -> status.value() == 400,
                            (request, res) -> {
                                throw new InvalidPostalCodeException("Invalid postcode: " + postalCode);
                            })
                    .onStatus(
                            status -> status.value() == 404,
                            (request, res) -> {
                                throw new RestaurantsNotFoundException("No restaurants found for postcode: " + postalCode);
                            })
                    .onStatus(
                            status -> status.is5xxServerError(),
                            (request, res) -> {
                                throw new UpstreamServiceException("Just Eat API is currently unavailable");
                            })
                    .body(RestaurantResponse.class);
            if (response == null || response.getRestaurants() == null || response.getRestaurants().isEmpty()) {
                throw new UpstreamServiceException("Received empty or invalid response from Just Eat API");
            }
            return response;
        } catch (RestClientException ex) {
            throw new UpstreamServiceException("Failed to call Just Eat API", ex);
        }
    }
}
