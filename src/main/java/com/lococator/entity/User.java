package com.lococator.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.sql.Blob;
import java.sql.Date;
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
    private Date joinDate;

    @Column(name = "EMPLOYEEID")
    private long employeeId;

    @Column(name = "IS_OWNER")
    private boolean isOwner;

    @Column(name = "HAS_ADMIN_PRIVILEGES")
    private boolean hasAdminPrivileges;

    @Column(name = "FOODTRUCKID")
    private long foodTruckId;

    @ManyToMany(cascade= CascadeType.ALL, fetch=FetchType.EAGER)
    @JoinTable(name="FAVORITES",
            joinColumns=@JoinColumn(name="USERID"),
            inverseJoinColumns=@JoinColumn(name="FOODTRUCKID"))
    private Set<FoodTruck> favorites;

    public User() {
    }

    public User(@Min(0) long id, @Email @NotNull String email, @NotNull @NotEmpty @Size(min = 4, max = 25) String username, String passwordHash, String passwordSalt, Date joinDate, long employeeId, boolean isOwner, boolean hasAdminPrivileges, long foodTruckId, Set<FoodTruck> favorites) {
        this.id = id;
        this.email = email;
        this.username = username;
        this.passwordHash = passwordHash;
        this.passwordSalt = passwordSalt;
        this.joinDate = joinDate;
        this.employeeId = employeeId;
        this.isOwner = isOwner;
        this.hasAdminPrivileges = hasAdminPrivileges;
        this.foodTruckId = foodTruckId;
        this.favorites = favorites;
    }

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

    public Date getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(Date joinDate) {
        this.joinDate = joinDate;
    }

    public long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(long employeeId) {
        this.employeeId = employeeId;
    }

    public boolean isOwner() {
        return isOwner;
    }

    public void setOwner(boolean owner) {
        isOwner = owner;
    }

    public boolean isHasAdminPrivileges() {
        return hasAdminPrivileges;
    }

    public void setHasAdminPrivileges(boolean hasAdminPrivileges) {
        this.hasAdminPrivileges = hasAdminPrivileges;
    }

    public long getFoodTruckId() {
        return foodTruckId;
    }

    public void setFoodTruckId(long foodTruckId) {
        this.foodTruckId = foodTruckId;
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
                ", employeeId=" + employeeId +
                ", isOwner=" + isOwner +
                ", hasAdminPrivileges=" + hasAdminPrivileges +
                ", foodTruckId=" + foodTruckId +
                ", favorites=" + favorites +
                '}';
    }
}
