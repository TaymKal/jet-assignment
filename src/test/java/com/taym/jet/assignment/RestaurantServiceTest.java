package com.taym.jet.assignment;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.Answers;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import com.taym.jet.assignment.exception.InvalidPostalCodeException;
import com.taym.jet.assignment.exception.UpstreamServiceException;
import com.taym.jet.assignment.model.Restaurant;
import com.taym.jet.assignment.model.RestaurantResponse;
import com.taym.jet.assignment.service.RestaurantService;
import com.taym.jet.assignment.validator.PostalCodeValidator;

public class RestaurantServiceTest {
    private final RestClient restClient = mock(RestClient.class, Answers.RETURNS_DEEP_STUBS);
    private final PostalCodeValidator postalCodeValidator = mock(PostalCodeValidator.class);
    private final RestaurantService restaurantService = new RestaurantService(restClient, postalCodeValidator);

    @Test
    void getUkRestaurants_shouldThrowInvalidPostalCode_whenPostalCodeIsNull() {
        assertThrows(InvalidPostalCodeException.class, () -> restaurantService.getUkRestaurants(null));
        verifyNoInteractions(postalCodeValidator, restClient);
    }

    @Test
    void getUkRestaurants_shouldThrowInvalidPostalCode_whenPostalCodeIsBlank() {
        assertThrows(InvalidPostalCodeException.class, () -> restaurantService.getUkRestaurants(" "));
        verifyNoInteractions(postalCodeValidator, restClient);
    }

    @Test
    void getUkRestaurants_shouldThrowInvalidPostalCode_whenValidatorRejectsPostalCode() {
        when(postalCodeValidator.isValidUkPostalCode("BAD123")).thenReturn(false);

        assertThrows(InvalidPostalCodeException.class, () -> restaurantService.getUkRestaurants("BAD123"));
        verifyNoInteractions(restClient);
    }

    @Test
    void getUkRestaurants_shouldWrapRestClientException() {
        when(postalCodeValidator.isValidUkPostalCode("EC4M7RF")).thenReturn(true);
        when(restClient.get()
                .uri("/discovery/uk/restaurants/enriched/bypostcode/{postcode}", "EC4M7RF")
                .retrieve()
                .onStatus(any(), any())
                .onStatus(any(), any())
                .onStatus(any(), any())
                .body(RestaurantResponse.class))
            .thenThrow(new RestClientException("network down"));

        assertThrows(UpstreamServiceException.class, () -> restaurantService.getUkRestaurants("EC4M7RF"));
    }

    @Test
    void getUkRestaurants_shouldThrowUpstreamException_whenResponseIsNull() {
        when(postalCodeValidator.isValidUkPostalCode("EC4M7RF")).thenReturn(true);
        when(restClient.get()
                .uri("/discovery/uk/restaurants/enriched/bypostcode/{postcode}", "EC4M7RF")
                .retrieve()
                .onStatus(any(), any())
                .onStatus(any(), any())
                .onStatus(any(), any())
                .body(RestaurantResponse.class))
            .thenReturn(null);

        assertThrows(UpstreamServiceException.class, () -> restaurantService.getUkRestaurants("EC4M7RF"));
    }

    @Test
    void getUkRestaurants_shouldThrowUpstreamException_whenRestaurantListIsNull() {
        when(postalCodeValidator.isValidUkPostalCode("EC4M7RF")).thenReturn(true);
        RestaurantResponse response = mock(RestaurantResponse.class);
        when(response.getRestaurants()).thenReturn(null);
        when(restClient.get()
                .uri("/discovery/uk/restaurants/enriched/bypostcode/{postcode}", "EC4M7RF")
                .retrieve()
                .onStatus(any(), any())
                .onStatus(any(), any())
                .onStatus(any(), any())
                .body(RestaurantResponse.class))
            .thenReturn(response);

        assertThrows(UpstreamServiceException.class, () -> restaurantService.getUkRestaurants("EC4M7RF"));
    }

    @Test
    void getUkRestaurants_shouldThrowUpstreamException_whenRestaurantListIsEmpty() {
        when(postalCodeValidator.isValidUkPostalCode("EC4M7RF")).thenReturn(true);
        RestaurantResponse response = mock(RestaurantResponse.class);
        when(response.getRestaurants()).thenReturn(Collections.emptyList());
        when(restClient.get()
                .uri("/discovery/uk/restaurants/enriched/bypostcode/{postcode}", "EC4M7RF")
                .retrieve()
                .onStatus(any(), any())
                .onStatus(any(), any())
                .onStatus(any(), any())
                .body(RestaurantResponse.class))
            .thenReturn(response);

        assertThrows(UpstreamServiceException.class, () -> restaurantService.getUkRestaurants("EC4M7RF"));
    }

    @Test
    void getUkRestaurants_shouldReturnResponse_whenResponseContainsRestaurants() {
        when(postalCodeValidator.isValidUkPostalCode("EC4M7RF")).thenReturn(true);
        RestaurantResponse response = mock(RestaurantResponse.class);
        when(response.getRestaurants()).thenReturn(List.of(mock(Restaurant.class)));
        when(restClient.get()
                .uri("/discovery/uk/restaurants/enriched/bypostcode/{postcode}", "EC4M7RF")
                .retrieve()
                .onStatus(any(), any())
                .onStatus(any(), any())
                .onStatus(any(), any())
                .body(RestaurantResponse.class))
            .thenReturn(response);

        RestaurantResponse result = restaurantService.getUkRestaurants("EC4M7RF");
        assertSame(response, result);
    }
}
