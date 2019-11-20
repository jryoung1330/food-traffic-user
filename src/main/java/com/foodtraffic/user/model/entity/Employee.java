package com.foodtraffic.user.model.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "EMPLOYEE")
@Data
public class Employee { // TODO: remove object when employee service is up

    @Id
    @Column(name = "USERID")
    private Long userId;

    @Column(name = "FOODTRUCKID")
    private Long foodTruckId;

    @Column(name = "IS_ASSOCIATE")
    private boolean isAssociate;

    @Column(name = "IS_ADMIN")
    private boolean isAdmin;

    @Column(name = "IS_OWNER")
    private boolean isOwner;

    @Column(name = "STATUS")
    private String status;

    @ManyToOne
    @JoinColumn(name = "FOODTRUCKID", updatable = false, insertable = false)
    private FoodTruck foodTruck;

}