package com.lococator.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Set;

@Entity
@Table(name="FOODTRUCK")
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

    @Column(name="COMPANY")
    @NotNull
    @NotEmpty
    @Size(max = 100)
    private String company;

    @Column(name = "LONGITUDEX")
    private Double longitude;

    @Column(name="LATITUDEY")
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

    @Column(name = "ZIPCODE")
    private int zipCode;

    @Column(name = "LOCATION_DETAILS")
    @Size(max = 1000)
    private String locationDetails;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "ASSOCIATE_KEY")
    @JsonIgnore
    private String associateKey;

    @OneToMany(mappedBy = "foodTruckId")
    private Set<User> ownerEmployees;

    public FoodTruck() {}

    public FoodTruck(@Min(0) Long id, @NotNull @NotEmpty @Size(max = 50) String name, @NotNull @NotEmpty @Size(max = 100) String company, Double longitude, Double latitude, @Size(max = 100) String streetAddress, @NotNull @NotEmpty @Size(max = 100) String city, @NotEmpty @NotNull @Size(min = 2, max = 2) String state, @NotNull @Size(max = 100) String county, int zipCode, @Size(max = 1000) String locationDetails, String description, String associateKey, Set<User> ownerEmployees) {
        this.id = id;
        this.name = name;
        this.company = company;
        this.longitude = longitude;
        this.latitude = latitude;
        this.streetAddress = streetAddress;
        this.city = city;
        this.state = state;
        this.county = county;
        this.zipCode = zipCode;
        this.locationDetails = locationDetails;
        this.description = description;
        this.associateKey = associateKey;
        this.ownerEmployees = ownerEmployees;
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

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
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

    public String getAssociateKey() {
        return associateKey;
    }

    public void setAssociateKey(String associateKey) {
        this.associateKey = associateKey;
    }

    public Set<User> getOwnerEmployees() {
        return ownerEmployees;
    }

    public void setOwnerEmployees(Set<User> Users) {
        this.ownerEmployees = Users;
    }

    @Override
    public String toString() {
        return "FoodTruck{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", company='" + company + '\'' +
                ", longitude=" + longitude +
                ", latitude=" + latitude +
                ", streetAddress='" + streetAddress + '\'' +
                ", city='" + city + '\'' +
                ", state='" + state + '\'' +
                ", county='" + county + '\'' +
                ", zipCode=" + zipCode +
                ", locationDetails='" + locationDetails + '\'' +
                ", description='" + description + '\'' +
                ", associateKey='" + associateKey + '\'' +
                ", ownerEmployees=" + ownerEmployees +
                '}';
    }
}
