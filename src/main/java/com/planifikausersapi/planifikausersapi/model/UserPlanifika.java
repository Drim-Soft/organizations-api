package com.planifikausersapi.planifikausersapi.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "UserPlanifika") 
public class UserPlanifika {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IDUser") 
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "photoURL")
    private String photoURL;

    @ManyToMany(mappedBy = "users")
    private List<Organization> organizations;

    // Getters y setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhotoURL() { return photoURL; }
    public void setPhotoURL(String photoURL) { this.photoURL = photoURL; }

    public List<Organization> getOrganizations() { return organizations; }
    public void setOrganizations(List<Organization> organizations) { this.organizations = organizations; }
}
