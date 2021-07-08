package com.foodtraffic.user.entity;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.ZonedDateTime;

@Entity
@Table(name="USER_ACCOUNT")
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USERID")
    private Long id;

    @Column(name = "FIRST_NAME")
    private String firstName;

    @Column(name = "LAST_NAME")
    private String lastName;

    @Email
    @Column(name = "EMAIL")
    private String email;

    @NotNull
    @Size(min = 4, max = 25)
    @Column(name = "USER_NAME")
    private String username;

    @Column(name = "PASSWORD_HASH")
    private String passwordHash;

    @Column(name = "PASSWORD_SALT")
    private String passwordSalt;

    @Column(name = "JOIN_DATE")
    private ZonedDateTime joinDate;

    @Column(name = "LAST_LOGIN")
    private ZonedDateTime lastLogin;

    @Column(name = "IS_EMAIL_VERIFIED")
    private boolean isEmailVerified;

    @Column(name = "VERIFICATION_CODE")
    private String verificationCode;

    @Column(name = "STATUS")
    private String status;

    @OneToOne
    @JoinColumn(name = "USERID", referencedColumnName = "id")
    private Employee employee;

}
