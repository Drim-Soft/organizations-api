package com.planifikausersapi.planifikausersapi.model;


import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "organizations")
public class Organization {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String name;

	private String address;
	private String phone;
	private String photoURL;

	@ManyToMany
	@JoinTable(
		name = "user_organization",
		joinColumns = @JoinColumn(name = "organization_id"),
		inverseJoinColumns = @JoinColumn(name = "user_id")
	)
	private List<UserPlanifika> users;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getPhotoURL() {
		return photoURL;
	}

	public void setPhotoURL(String photoURL) {
		this.photoURL = photoURL;
	}

	public List<UserPlanifika> getUsers() {
		return users;
	}

	public void setUsers(List<UserPlanifika> users) {
		this.users = users;
	}
}
