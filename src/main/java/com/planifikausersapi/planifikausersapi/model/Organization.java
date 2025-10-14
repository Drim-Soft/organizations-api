package com.planifikausersapi.planifikausersapi.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "Organization") 
public class Organization {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IDOrganization") 
    private Long IDOrganization;

    @Column(name = "nit", nullable = false)
    private String nit;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "address")
    private String address;

    @Column(name = "phone")
    private String phone;

    @Column(name = "photoURL")
    private String photoURL;

    @ManyToMany
    @JoinTable(
        name = "UserOrganization",
        joinColumns = @JoinColumn(name = "IDOrganization"),
        inverseJoinColumns = @JoinColumn(name = "IDUser") 
    )
    private List<UserPlanifika> users;

    // Getters y setters
    public Long getId() { return IDOrganization; }
    public void setId(Long IDOrganization) { this.IDOrganization = IDOrganization; }

    public String getNit() { return nit; }
    public void setNit(String nit) { this.nit = nit; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getPhotoURL() { return photoURL; }
    public void setPhotoURL(String photoURL) { this.photoURL = photoURL; }

    public List<UserPlanifika> getUsers() { return users; }
    public void setUsers(List<UserPlanifika> users) { this.users = users; }
}
