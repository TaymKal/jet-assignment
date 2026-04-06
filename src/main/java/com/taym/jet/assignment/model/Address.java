package com.taym.jet.assignment.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class Address {
    private String city;
    private String firstLine;
    private String postalCode;
    private Location location;
}
