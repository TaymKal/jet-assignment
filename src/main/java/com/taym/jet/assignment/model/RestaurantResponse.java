package com.taym.jet.assignment.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class RestaurantResponse {
    private List<Restaurant> restaurants;
}
