package com.taym.jet.assignment.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;


@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class Cuisine {
    private String name;
}
