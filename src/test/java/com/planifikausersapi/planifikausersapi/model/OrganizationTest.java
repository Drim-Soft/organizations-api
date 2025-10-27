package com.planifikausersapi.planifikausersapi.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

class OrganizationTest {

    private Organization organization;

    @BeforeEach
    void setUp() {
        organization = new Organization();
    }

    @Test
    void testGettersAndSetters() {
        // Test ID
        Long id = 1L;
        organization.setId(id);
        assertEquals(id, organization.getId());

        // Test NIT
        String nit = "12345678-9";
        organization.setNit(nit);
        assertEquals(nit, organization.getNit());

        // Test Name
        String name = "Empresa Test";
        organization.setName(name);
        assertEquals(name, organization.getName());

        // Test Address
        String address = "Calle 123 #45-67";
        organization.setAddress(address);
        assertEquals(address, organization.getAddress());

        // Test Phone
        String phone = "+57 300 123 4567";
        organization.setPhone(phone);
        assertEquals(phone, organization.getPhone());

        // Test PhotoURL
        String photoURL = "https://example.com/photo.jpg";
        organization.setPhotoURL(photoURL);
        assertEquals(photoURL, organization.getPhotoURL());

        // Test Domain
        String domain = "empresa.com";
        organization.setDomain(domain);
        assertEquals(domain, organization.getDomain());
    }

    @Test
    void testUsersRelationship() {
        List<UserPlanifika> users = new ArrayList<>();
        
        UserPlanifika user1 = new UserPlanifika();
        user1.setId(1L);
        user1.setName("Usuario 1");
        
        UserPlanifika user2 = new UserPlanifika();
        user2.setId(2L);
        user2.setName("Usuario 2");
        
        users.add(user1);
        users.add(user2);
        
        organization.setUsers(users);
        
        assertEquals(2, organization.getUsers().size());
        assertEquals("Usuario 1", organization.getUsers().get(0).getName());
        assertEquals("Usuario 2", organization.getUsers().get(1).getName());
    }

    @Test
    void testOrganizationWithAllFields() {
        Long id = 1L;
        String nit = "12345678-9";
        String name = "Empresa Completa";
        String address = "Calle Principal 123";
        String phone = "+57 300 123 4567";
        String photoURL = "https://example.com/logo.jpg";
        String domain = "empresa.com";

        organization.setId(id);
        organization.setNit(nit);
        organization.setName(name);
        organization.setAddress(address);
        organization.setPhone(phone);
        organization.setPhotoURL(photoURL);
        organization.setDomain(domain);

        assertEquals(id, organization.getId());
        assertEquals(nit, organization.getNit());
        assertEquals(name, organization.getName());
        assertEquals(address, organization.getAddress());
        assertEquals(phone, organization.getPhone());
        assertEquals(photoURL, organization.getPhotoURL());
        assertEquals(domain, organization.getDomain());
    }

    @Test
    void testNullValues() {
        organization.setId(null);
        organization.setNit(null);
        organization.setName(null);
        organization.setAddress(null);
        organization.setPhone(null);
        organization.setPhotoURL(null);
        organization.setDomain(null);
        organization.setUsers(null);

        assertNull(organization.getId());
        assertNull(organization.getNit());
        assertNull(organization.getName());
        assertNull(organization.getAddress());
        assertNull(organization.getPhone());
        assertNull(organization.getPhotoURL());
        assertNull(organization.getDomain());
        assertNull(organization.getUsers());
    }
}
