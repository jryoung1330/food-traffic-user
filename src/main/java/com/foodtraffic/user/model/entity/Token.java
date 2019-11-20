package com.foodtraffic.user.model.entity;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "USER_TOKEN")
@Data
public class Token {

    @Id
    @Column(name="TOKENID", columnDefinition = "SERIAL")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotNull
    @Column(name = "TOKEN_CODE")
    public String tokenCode;

    @NotNull
    @Column(name = "USERID")
    public Long userId;

}
