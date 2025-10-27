package com.planifikausersapi.planifikausersapi.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

class UserPlanifikaTest {

    private UserPlanifika userPlanifika;
    private Organization organization;

    @BeforeEach
    void setUp() {
        userPlanifika = new UserPlanifika();
        organization = new Organization();
        organization.setId(1L);
        organization.setName("Empresa Test");
        organization.setNit("12345678-9");
    }

    @Test
    void testGettersAndSetters() {
        // Test ID
        Long id = 1L;
        userPlanifika.setId(id);
        assertEquals(id, userPlanifika.getId());

        // Test Name
        String name = "Juan Pérez";
        userPlanifika.setName(name);
        assertEquals(name, userPlanifika.getName());

        // Test PhotoURL
        String photoURL = "https://example.com/user-photo.jpg";
        userPlanifika.setPhotoURL(photoURL);
        assertEquals(photoURL, userPlanifika.getPhotoURL());
    }

    @Test
    void testOrganizationRelationship() {
        userPlanifika.setOrganization(organization);
        
        assertEquals(organization, userPlanifika.getOrganization());
        assertEquals(1L, userPlanifika.getOrganization().getId());
        assertEquals("Empresa Test", userPlanifika.getOrganization().getName());
        assertEquals("12345678-9", userPlanifika.getOrganization().getNit());
    }

    @Test
    void testUserWithAllFields() {
        Long id = 1L;
        String name = "María García";
        String photoURL = "https://example.com/maria.jpg";

        userPlanifika.setId(id);
        userPlanifika.setName(name);
        userPlanifika.setPhotoURL(photoURL);
        userPlanifika.setOrganization(organization);

        assertEquals(id, userPlanifika.getId());
        assertEquals(name, userPlanifika.getName());
        assertEquals(photoURL, userPlanifika.getPhotoURL());
        assertEquals(organization, userPlanifika.getOrganization());
    }

    @Test
    void testNullValues() {
        userPlanifika.setId(null);
        userPlanifika.setName(null);
        userPlanifika.setPhotoURL(null);
        userPlanifika.setOrganization(null);

        assertNull(userPlanifika.getId());
        assertNull(userPlanifika.getName());
        assertNull(userPlanifika.getPhotoURL());
        assertNull(userPlanifika.getOrganization());
    }

    @Test
    void testEmptyStringValues() {
        userPlanifika.setName("");
        userPlanifika.setPhotoURL("");

        assertEquals("", userPlanifika.getName());
        assertEquals("", userPlanifika.getPhotoURL());
    }

    @Test
    void testUserWithDifferentOrganization() {
        Organization organization2 = new Organization();
        organization2.setId(2L);
        organization2.setName("Otra Empresa");
        organization2.setNit("98765432-1");

        userPlanifika.setOrganization(organization);
        assertEquals(organization, userPlanifika.getOrganization());

        userPlanifika.setOrganization(organization2);
        assertEquals(organization2, userPlanifika.getOrganization());
        assertEquals(2L, userPlanifika.getOrganization().getId());
        assertEquals("Otra Empresa", userPlanifika.getOrganization().getName());
    }
}
