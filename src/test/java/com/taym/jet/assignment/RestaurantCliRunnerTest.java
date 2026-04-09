package com.taym.jet.assignment;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.taym.jet.assignment.cli.RestaurantCliRunner;
import com.taym.jet.assignment.exception.InvalidPostalCodeException;
import com.taym.jet.assignment.model.Restaurant;
import com.taym.jet.assignment.model.RestaurantResponse;
import com.taym.jet.assignment.service.RestaurantService;

class RestaurantCliRunnerTest {

    @Test
    void cli_exitsImmediately_whenUserTypesExit() throws Exception {
        RestaurantService restaurantService = mock(RestaurantService.class);
        RestaurantCliRunner runner = new RestaurantCliRunner(restaurantService, 10);

        InputStream originalIn = System.in;
        PrintStream originalOut = System.out;
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        try {
            System.setIn(new ByteArrayInputStream("exit\n".getBytes(StandardCharsets.UTF_8)));
            System.setOut(new PrintStream(output));

            runner.run();
        } finally {
            System.setIn(originalIn);
            System.setOut(originalOut);
        }

        String console = output.toString(StandardCharsets.UTF_8);
        assertTrue(console.contains("Enter UK postcode (or type 'exit' to quit): "));
        assertTrue(console.contains("Goodbye!"));
        verifyNoInteractions(restaurantService);
    }

    @Test
    void cli_retriesAfterInvalidPostcode_andExitsOnExitCommand() throws Exception {
        RestaurantService restaurantService = mock(RestaurantService.class);

        when(restaurantService.getUkRestaurants("BAD123"))
                .thenThrow(new InvalidPostalCodeException("BAD123"));

        RestaurantResponse response = mock(RestaurantResponse.class);
        Restaurant restaurant = mock(Restaurant.class);
        when(response.getRestaurants()).thenReturn(List.of(restaurant));
        when(restaurant.getName()).thenReturn("Test Pizza");
        when(restaurant.getCuisines()).thenReturn(List.of());
        when(restaurant.getRating()).thenReturn(null);
        when(restaurant.getAddress()).thenReturn(null);
        when(restaurantService.getUkRestaurants("EC4M7RF")).thenReturn(response);

        RestaurantCliRunner runner = new RestaurantCliRunner(restaurantService, 10);

        InputStream originalIn = System.in;
        PrintStream originalOut = System.out;
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        try {
            System.setIn(new ByteArrayInputStream("BAD123\nEC4M7RF\nexit\n".getBytes(StandardCharsets.UTF_8)));
            System.setOut(new PrintStream(output));

            runner.run();
        } finally {
            System.setIn(originalIn);
            System.setOut(originalOut);
        }

        String console = output.toString(StandardCharsets.UTF_8);
        assertTrue(console.contains("Enter UK postcode (or type 'exit' to quit): "));
        assertTrue(console.contains("Invalid postcode. Please try again."));
        assertTrue(console.contains("1. Test Pizza"));
        assertTrue(console.contains("Goodbye!"));

        verify(restaurantService).getUkRestaurants("BAD123");
        verify(restaurantService).getUkRestaurants("EC4M7RF");
    }
}
