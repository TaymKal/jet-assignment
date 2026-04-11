package com.taym.jet.assignment.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class Restaurant {
    private String id;
    private Long defaultDisplayRank;
    private String name;
    private List<Cuisine> cuisines;
    private Rating rating; 
    private Address address;
}
