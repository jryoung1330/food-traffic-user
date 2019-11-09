package com.foodtraffic.user.model.entity;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name="FOOD_TRUCK")
public class FoodTruck {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="FOODTRUCKID")
    @Min(0)
    private Long id;

    @Column(name="NAME")
    @NotNull
    @NotEmpty
    @Size(max = 50)
    private String name;

    @Column(name = "LONGITUDE")
    private Double longitude;

    @Column(name="LATITUDE")
    private Double latitude;

    @Column(name = "STREET_ADDRESS")
    @Size(max = 100)
    private String streetAddress;

    @Column(name = "CITY")
    @NotNull
    @NotEmpty
    @Size(max = 100)
    private String city;

    @Column(name = "STATE")
    @NotEmpty
    @NotNull
    @Size(min = 2, max = 2)
    private String state;

    @Column(name = "COUNTY")
    @NotNull
    @Size(max = 100)
    private String county;

    @Column(name = "ZIP_CODE")
    private int zipCode;

    @Column(name = "LOCATION_DETAILS")
    @Size(max = 1000)
    private String locationDetails;

    @Column(name = "DESCRIPTION")
    private String description;

    public FoodTruck() {}

    public FoodTruck(@Min(0) Long id, @NotNull @NotEmpty @Size(max = 50) String name, Double longitude, Double latitude, @Size(max = 100) String streetAddress, @NotNull @NotEmpty @Size(max = 100) String city, @NotEmpty @NotNull @Size(min = 2, max = 2) String state, @NotNull @Size(max = 100) String county, int zipCode, @Size(max = 1000) String locationDetails, String description) {
        this.id = id;
        this.name = name;
        this.longitude = longitude;
        this.latitude = latitude;
        this.streetAddress = streetAddress;
        this.city = city;
        this.state = state;
        this.county = county;
        this.zipCode = zipCode;
        this.locationDetails = locationDetails;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public String getStreetAddress() {
        return streetAddress;
    }

    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public int getZipCode() {
        return zipCode;
    }

    public void setZipCode(int zipCode) {
        this.zipCode = zipCode;
    }

    public String getLocationDetails() {
        return locationDetails;
    }

    public void setLocationDetails(String locationDetails) {
        this.locationDetails = locationDetails;
    }

    public String getDescription() { return description; }

    public void setDescription(String description) { this.description = description; }

    @Override
    public String toString() {
        return "FoodTruck{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", longitude=" + longitude +
                ", latitude=" + latitude +
                ", streetAddress='" + streetAddress + '\'' +
                ", city='" + city + '\'' +
                ", state='" + state + '\'' +
                ", county='" + county + '\'' +
                ", zipCode=" + zipCode +
                ", locationDetails='" + locationDetails + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
