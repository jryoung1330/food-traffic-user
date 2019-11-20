package com.foodtraffic.user.model.dto;

import lombok.Data;

@Data
public class FoodTruckDto {

    private String foodTruckName;

    private String displayName;

    private Double longitude;

    private Double latitude;

    private String streetAddress;

    private String city;

    private String state;

    private String county;

    private int zipCode;

    private String locationDetails;

    private String description;

}
