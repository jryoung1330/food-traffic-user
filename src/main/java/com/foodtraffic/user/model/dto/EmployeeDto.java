package com.foodtraffic.user.model.dto;


import lombok.Data;

@Data
public class EmployeeDto {

    private boolean isAssociate;

    private boolean isAdmin;

    private boolean isOwner;

    private String status;

    private FoodTruckDto foodTruck;

}
