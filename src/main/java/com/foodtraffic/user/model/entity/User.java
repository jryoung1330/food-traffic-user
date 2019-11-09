package com.foodtraffic.user.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name="GENERAL_USER")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USERID")
    private long id;

    @Email
    @Column(name = "EMAIL")
    private String email;

    @NotNull
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

    @JsonIgnore
    @Column(name = "STATUS")
    private String status;

    @JsonIgnore
    @Column(name = "IS_EMAIL_VERIFIED")
    private boolean isEmailVerified;

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

    public boolean getIsEmailVerified() {
        return isEmailVerified;
    }

    public void setIsEmailVerified(boolean isEmailVerified) {
        this.isEmailVerified = isEmailVerified;
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
                ", isEmailVerified='" + isEmailVerified + '\'' +
                ", favorites=" + favorites +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id == user.id &&
                isEmailVerified == user.isEmailVerified &&
                Objects.equals(email, user.email) &&
                Objects.equals(username, user.username) &&
                Objects.equals(passwordHash, user.passwordHash) &&
                Objects.equals(passwordSalt, user.passwordSalt) &&
                Objects.equals(joinDate, user.joinDate) &&
                Objects.equals(lastLogin, user.lastLogin) &&
                Objects.equals(status, user.status) &&
                Objects.equals(favorites, user.favorites);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email, username, passwordHash, passwordSalt, joinDate, lastLogin, status, isEmailVerified, favorites);
    }
}
