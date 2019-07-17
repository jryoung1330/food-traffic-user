package com.lococator.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "USER_TOKEN")
public class Token {

    @Id
    @Column(name="TOKENID", columnDefinition = "SERIAL")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "TOKEN_CODE")
    @NotNull
    public String tokenCode;

    @Column(name = "USERID")
    @NotNull
    public Long userId;

    public Token() {
    }

    public Token(int id, @NotNull String tokenCode, @NotNull Long userId) {
        this.id = id;
        this.tokenCode = tokenCode;
        this.userId = userId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTokenCode() {
        return tokenCode;
    }

    public void setTokenCode(String tokenCode) {
        this.tokenCode = tokenCode;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "Token{" +
                "id=" + id +
                ", tokenCode='" + tokenCode + '\'' +
                ", userId=" + userId +
                '}';
    }
}
