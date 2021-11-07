package com.foodtraffic.user.entity;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
@Table(name = "EMPLOYEE")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "EMPLOYEEID")
    private Long id;

    @Column(name = "VENDORID")
    private Long vendorId;

    @Column(name = "IS_ASSOCIATE")
    private boolean isAssociate;

    @Column(name = "IS_ADMIN")
    private boolean isAdmin;

    @OneToOne (mappedBy = "employee")
    private User user;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Employee employee = (Employee) o;
        return id != null && Objects.equals(id, employee.id);
    }

    @Override
    public int hashCode() {
        return 0;
    }
}

