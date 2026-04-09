package com.taym.jet.assignment.cli;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.taym.jet.assignment.exception.InvalidPostalCodeException;
import com.taym.jet.assignment.exception.UpstreamServiceException;
import com.taym.jet.assignment.model.Cuisine;
import com.taym.jet.assignment.model.Restaurant;
import com.taym.jet.assignment.model.RestaurantResponse;
import com.taym.jet.assignment.service.RestaurantService;
import com.taym.jet.assignment.view.RestaurantView;

@Component
@Profile("!test")
public class RestaurantCliRunner implements CommandLineRunner {
    private final RestaurantService restaurantService;
    private final int restaurantLimit;

    public RestaurantCliRunner(
            RestaurantService restaurantService,
            @Value("${app.output.restaurant-limit:10}") int restaurantLimit) {
        this.restaurantService = restaurantService;
        this.restaurantLimit = restaurantLimit;
    }

    @Override
    public void run(String... args) {
        String nextPostalCode = (args.length > 0 && args[0] != null) ? args[0].trim() : null;

        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                String postalCode = readPostalCode(scanner, nextPostalCode);
                nextPostalCode = null;

        
                if (isExitCommand(postalCode)) {
                    System.out.println("Goodbye!");
                    break;
                }

                try {
                    RestaurantResponse response = restaurantService.getUkRestaurants(postalCode);
                    List<RestaurantView> topRestaurants = buildTopRestaurants(response);
                    printRestaurants(postalCode, topRestaurants);
                } catch (InvalidPostalCodeException e) {
                    System.out.println("Invalid postcode. Please try again.");
                } catch (UpstreamServiceException e) {
                    System.out.println("Could not retrieve restaurants right now. Please try again later.");
                }
            }
        }
    }

    private String readPostalCode(Scanner scanner, String initialPostalCode) {
        if (initialPostalCode != null && !initialPostalCode.isEmpty()) {
            return initialPostalCode;
        }

        System.out.print("Enter UK postcode (or type 'exit' to quit): ");
        
        
        return scanner.nextLine().trim();
       
    }

    private boolean isExitCommand(String value) {
        return "exit".equalsIgnoreCase(value);
    }

    private List<RestaurantView> buildTopRestaurants(RestaurantResponse response) {
        List<RestaurantView> topRestaurants = new ArrayList<>();
        for (Restaurant restaurant : response.getRestaurants()) {
            if (topRestaurants.size() == restaurantLimit) {
                break;
            }
            topRestaurants.add(toRestaurantView(restaurant));
        }
        return topRestaurants;
    }

    private RestaurantView toRestaurantView(Restaurant restaurant) {
        return new RestaurantView(
                restaurant.getName(),
                toCuisineNames(restaurant.getCuisines()),
                restaurant.getRating() != null ? restaurant.getRating().getStarRating() : null,
                restaurant.getAddress());
    }

    private List<String> toCuisineNames(List<Cuisine> cuisines) {
        List<String> names = new ArrayList<>();
        if (cuisines == null) {
            return names;
        }
        for (Cuisine cuisine : cuisines) {
            names.add(cuisine.getName());
        }
        return names;
    }

    private void printRestaurants(String postalCode, List<RestaurantView> topRestaurants) {
        System.out.println("\n" + "Here are " + topRestaurants.size() + " restaurants for " + postalCode + ":" + "\n");
        int index = 1;
        for (RestaurantView restaurantView : topRestaurants) {
            printRestaurantLine(index, restaurantView);
            index++;
        }
    }

    private void printRestaurantLine(int index, RestaurantView restaurantView) {
        String addressLine = restaurantView.getAddress() != null ? restaurantView.getAddress().getFirstLine() : "N/A";
        String addressPostcode = restaurantView.getAddress() != null ? restaurantView.getAddress().getPostalCode() : "N/A";

        System.out.println(index + ". " + restaurantView.getName()
                + " | Rating: " + restaurantView.getRating()
                + " | Cuisines: " + restaurantView.getCuisines()
                + " | Address: " + addressLine + ", " + addressPostcode);
        System.out.println();
    }
}
