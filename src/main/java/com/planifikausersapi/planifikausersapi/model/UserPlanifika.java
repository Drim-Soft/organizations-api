package com.planifikausersapi.planifikausersapi.model;

import jakarta.persistence.*;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "UserPlanifika")
public class UserPlanifika {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IDUser")
    private Long IDUser;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "photoURL")
    private String photoURL;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "IDOrganization", nullable = false)
    private Organization organization;

    // Getters y setters
    public Long getId() {
        return IDUser;
    }

    public void setId(Long IDUser) {
        this.IDUser = IDUser;
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

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }
}
