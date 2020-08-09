package com.foodtraffic.user.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "FAVORITE")
@Data
public class Favorite {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "FAVORITEID")
	private Long id;
	
	@Column(name = "VENDORID")
	private Long vendorId;
	
	@Column(name = "USERID")
	private Long userId;
	
}
