package com.foodtraffic.user.entity;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
@Table(name = "FAVORITE")
public class Favorite {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "FAVORITEID")
	private Long id;

	@Column(name = "VENDORID")
	private Long vendorId;

	@Column(name = "USERID")
	private Long userId;

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
		Favorite favorite = (Favorite) o;
		return id != null && Objects.equals(id, favorite.id);
	}

	@Override
	public int hashCode() {
		return 0;
	}
}

