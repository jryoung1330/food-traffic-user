package com.foodtraffic.user.model.dto;

import java.util.Objects;
import java.util.Set;

public class UserDto {

    private String email;

    private String username;

    private Set<FoodTruckDto> favorites;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Set<FoodTruckDto> getFavorites() {
        return favorites;
    }

    public void setFavorites(Set<FoodTruckDto> favorites) {
        this.favorites = favorites;
    }

    @Override
    public String toString() {
        return "UserDto{" +
                "email='" + email + '\'' +
                ", username='" + username + '\'' +
                ", favorites=" + favorites +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserDto userDto = (UserDto) o;
        return Objects.equals(email, userDto.email) &&
                Objects.equals(username, userDto.username) &&
                Objects.equals(favorites, userDto.favorites);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email, username, favorites);
    }
}
