package com.foodtraffic.user.model.entity;

import lombok.Data;
import javax.persistence.*;
import javax.validation.constraints.*;
import java.time.ZonedDateTime;
import java.util.Set;

@Entity
@Table(name="FTUSER")
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "FTUSERID")
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

    @Column(name = "PASSWORD_SALT")
    private String passwordSalt;

    @Column(name = "JOIN_DATE")
    private ZonedDateTime joinDate;

    @Column(name = "LAST_LOGIN")
    private ZonedDateTime lastLogin;

    @Column(name = "STATUS")
    private Integer status;

    @Column(name = "IS_EMAIL_VERIFIED")
    private boolean isEmailVerified;

    @OneToOne
    @PrimaryKeyJoinColumn
    private Employee employee; // TODO: remove annotations replace with call to employee service

    @ManyToMany(cascade= CascadeType.ALL, fetch=FetchType.EAGER)
    @JoinTable(name="FAVORITE",
            joinColumns=@JoinColumn(name="USERID"),
            inverseJoinColumns=@JoinColumn(name="FOODTRUCKID"))
    private Set<FoodTruck> favorites;

}
