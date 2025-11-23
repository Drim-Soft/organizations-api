package com.planifikausersapi.planifikausersapi.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Pruebas unitarias para UserPlanifika")
class UserPlanifikaTest {

    private UserPlanifika user;
    private Organization organization;

    @BeforeEach
    void setUp() {
        user = new UserPlanifika();
        organization = new Organization();
        organization.setId(1L);
        organization.setName("Organización Test");
    }

    @Test
    @DisplayName("Debería obtener y establecer el ID correctamente")
    void testId() {
        Long id = 1L;
        user.setId(id);
        assertEquals(id, user.getId());
    }

    @Test
    @DisplayName("Debería obtener y establecer el nombre correctamente")
    void testName() {
        String name = "Usuario Test";
        user.setName(name);
        assertEquals(name, user.getName());
    }

    @Test
    @DisplayName("Debería obtener y establecer la URL de foto correctamente")
    void testPhotoURL() {
        String photoURL = "http://example.com/user.jpg";
        user.setPhotoURL(photoURL);
        assertEquals(photoURL, user.getPhotoURL());
    }

    @Test
    @DisplayName("Debería obtener y establecer la organización correctamente")
    void testOrganization() {
        user.setOrganization(organization);
        assertNotNull(user.getOrganization());
        assertEquals(organization.getId(), user.getOrganization().getId());
        assertEquals(organization.getName(), user.getOrganization().getName());
    }

    @Test
    @DisplayName("Debería manejar organización nula correctamente")
    void testOrganization_Null() {
        user.setOrganization(null);
        assertNull(user.getOrganization());
    }
}

