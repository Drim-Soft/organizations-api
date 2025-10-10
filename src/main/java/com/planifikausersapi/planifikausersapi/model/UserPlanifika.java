package com.planifikausersapi.planifikausersapi.model;
import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "user_planifika")
public class UserPlanifika {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;
	private String photoURL;

	@ManyToMany(mappedBy = "users")
	private List<Organization> organizations;

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

	public String getPhotoURL() {
		return photoURL;
	}

	public void setPhotoURL(String photoURL) {
		this.photoURL = photoURL;
	}

	public List<Organization> getOrganizations() {
		return organizations;
	}

	public void setOrganizations(List<Organization> organizations) {
		this.organizations = organizations;
	}
}
