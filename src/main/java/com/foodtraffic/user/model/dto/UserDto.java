package com.foodtraffic.user.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.Set;

@Data
public class UserDto {

    private Long id;

    private String email;

    private String username;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private EmployeeDto employee;

    private Set<FoodTruckDto> favorites;

}
