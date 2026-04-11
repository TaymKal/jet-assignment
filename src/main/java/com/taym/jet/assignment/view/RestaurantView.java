package com.taym.jet.assignment.view;

import java.util.List;

import com.taym.jet.assignment.model.Address;

public class RestaurantView {
    private String name;
    private List<String> cuisines;
    private Double rating;
    private Address address;

    public RestaurantView(String name, List<String> cuisines, Double rating, Address address) {
        this.name = name;
        this.cuisines = cuisines;
        this.rating = rating;
        this.address = address;
    }

    public String getName() { return name; }
    public List<String> getCuisines() { return cuisines; }
    public Double getRating() { return rating; }
    public Address getAddress() { return address; }
}