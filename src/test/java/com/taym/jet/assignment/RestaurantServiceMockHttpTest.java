package com.taym.jet.assignment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import com.taym.jet.assignment.exception.InvalidPostalCodeException;
import com.taym.jet.assignment.exception.UpstreamServiceException;
import com.taym.jet.assignment.model.RestaurantResponse;
import com.taym.jet.assignment.service.RestaurantService;
import com.taym.jet.assignment.validator.PostalCodeValidator;

class RestaurantServiceMockHttpTest {

    private MockRestServiceServer server;
    private RestaurantService restaurantService;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder().baseUrl("https://api.test");
        server = MockRestServiceServer.bindTo(builder).build();
        restaurantService = new RestaurantService(builder.build(), new PostalCodeValidator());
    }

    @AfterEach
    void tearDown() {
        server.verify();
    }

    @Test
    void getUkRestaurants_returnsRestaurantResponseFromMockServer() {
        String json = """
                {
                  "restaurants": [
                    {
                      "name": "Test Pizza",
                      "cuisines": [{ "name": "Pizza" }],
                      "rating": { "starRating": 4.5 },
                      "address": {
                        "city": "London",
                        "firstLine": "1 Test St",
                        "postalCode": "EC4M 7RF",
                        "location": { "type": "Point", "coordinates": [-0.1, 51.5] }
                      }
                    }
                  ]
                }
                """;

        server.expect(requestTo("https://api.test/discovery/uk/restaurants/enriched/bypostcode/EC4M7RF"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(json, MediaType.APPLICATION_JSON));

        RestaurantResponse response = restaurantService.getUkRestaurants("EC4M7RF");

        assertEquals(1, response.getRestaurants().size());
        assertEquals("Test Pizza", response.getRestaurants().get(0).getName());
    }

    @Test
    void getUkRestaurants_throwsWhenUpstreamReturnsEmptyList() {
        String json = """
                { "restaurants": [] }
                """;

        server.expect(requestTo("https://api.test/discovery/uk/restaurants/enriched/bypostcode/EC4M7RF"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(json, MediaType.APPLICATION_JSON));

        UpstreamServiceException exception =
                assertThrows(UpstreamServiceException.class, () -> restaurantService.getUkRestaurants("EC4M7RF"));
        assertTrue(exception.getMessage().contains("empty or invalid response"));
    }

    @Test
    void getUkRestaurants_throwsInvalidPostalCode_beforeCallingUpstream() {
        assertThrows(InvalidPostalCodeException.class, () -> restaurantService.getUkRestaurants("BAD123"));
    }

    @Test
    void getUkRestaurants_throwsInvalidPostalCode_whenUpstreamReturns400() {
        server.expect(requestTo("https://api.test/discovery/uk/restaurants/enriched/bypostcode/EC4M7RF"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.BAD_REQUEST));

        assertThrows(InvalidPostalCodeException.class, () -> restaurantService.getUkRestaurants("EC4M7RF"));
    }

    @Test
    void getUkRestaurants_throwsUpstreamServiceException_whenUpstreamReturns500() {
        server.expect(requestTo("https://api.test/discovery/uk/restaurants/enriched/bypostcode/EC4M7RF"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR));

        UpstreamServiceException exception =
                assertThrows(UpstreamServiceException.class, () -> restaurantService.getUkRestaurants("EC4M7RF"));
        assertTrue(exception.getMessage().contains("Just Eat API is currently unavailable"));
    }
}
