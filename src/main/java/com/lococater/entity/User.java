package com.lococater.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.sql.Date;
import java.time.ZonedDateTime;
import java.util.Set;

@Entity
@Table(name="GENERAL_USER")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USERID")
    @Min(0)
    private long id;

    @Email
    @NotNull
    @Column(name = "EMAIL")
    private String email;

    @NotNull
    @NotEmpty
    @Size(min = 4, max = 25)
    @Column(name = "USERNAME")
    private String username;

    @Column(name = "PASSWORD_HASH")
    private String passwordHash;

    @JsonIgnore
    @Column(name = "PASSWORD_SALT")
    private String passwordSalt;

    @Column(name = "JOIN_DATE")
    private ZonedDateTime joinDate;

    @Column(name = "LAST_LOGIN")
    private ZonedDateTime lastLogin;

    @Column(name = "STATUS")
    private String status;

    @ManyToMany(cascade= CascadeType.ALL, fetch=FetchType.EAGER)
    @JoinTable(name="FAVORITE",
            joinColumns=@JoinColumn(name="USERID"),
            inverseJoinColumns=@JoinColumn(name="FOODTRUCKID"))
    private Set<FoodTruck> favorites;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

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

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getPasswordSalt() {
        return passwordSalt;
    }

    public void setPasswordSalt(String passwordSalt) {
        this.passwordSalt = passwordSalt;
    }

    public ZonedDateTime getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(ZonedDateTime joinDate) {
        this.joinDate = joinDate;
    }

    public ZonedDateTime getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(ZonedDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Set<FoodTruck> getFavorites() {
        return favorites;
    }

    public void setFavorites(Set<FoodTruck> favorites) {
        this.favorites = favorites;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", username='" + username + '\'' +
                ", passwordHash='" + passwordHash + '\'' +
                ", passwordSalt='" + passwordSalt + '\'' +
                ", joinDate=" + joinDate +
                ", lastLogin=" + lastLogin +
                ", status='" + status + '\'' +
                ", favorites=" + favorites +
                '}';
    }
}
