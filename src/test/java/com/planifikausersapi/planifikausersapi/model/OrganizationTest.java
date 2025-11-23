package com.planifikausersapi.planifikausersapi.model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Pruebas unitarias para Organization")
class OrganizationTest {

    private Organization organization;
    private UserPlanifika user1;
    private UserPlanifika user2;

    @BeforeEach
    void setUp() {
        organization = new Organization();
        user1 = new UserPlanifika();
        user1.setId(1L);
        user1.setName("Usuario 1");
        
        user2 = new UserPlanifika();
        user2.setId(2L);
        user2.setName("Usuario 2");
    }

    @Test
    @DisplayName("Debería obtener y establecer el ID correctamente")
    void testId() {
        Long id = 1L;
        organization.setId(id);
        assertEquals(id, organization.getId());
    }

    @Test
    @DisplayName("Debería obtener y establecer el NIT correctamente")
    void testNit() {
        String nit = "123456789";
        organization.setNit(nit);
        assertEquals(nit, organization.getNit());
    }

    @Test
    @DisplayName("Debería obtener y establecer el nombre correctamente")
    void testName() {
        String name = "Organización Test";
        organization.setName(name);
        assertEquals(name, organization.getName());
    }

    @Test
    @DisplayName("Debería obtener y establecer la dirección correctamente")
    void testAddress() {
        String address = "Calle Test 123";
        organization.setAddress(address);
        assertEquals(address, organization.getAddress());
    }

    @Test
    @DisplayName("Debería obtener y establecer el teléfono correctamente")
    void testPhone() {
        String phone = "1234567890";
        organization.setPhone(phone);
        assertEquals(phone, organization.getPhone());
    }

    @Test
    @DisplayName("Debería obtener y establecer la URL de foto correctamente")
    void testPhotoURL() {
        String photoURL = "http://example.com/photo.jpg";
        organization.setPhotoURL(photoURL);
        assertEquals(photoURL, organization.getPhotoURL());
    }

    @Test
    @DisplayName("Debería obtener y establecer el dominio correctamente")
    void testDomain() {
        String domain = "test.com";
        organization.setDomain(domain);
        assertEquals(domain, organization.getDomain());
    }

    @Test
    @DisplayName("Debería obtener y establecer la lista de usuarios correctamente")
    void testUsers() {
        List<UserPlanifika> users = new ArrayList<>();
        users.add(user1);
        users.add(user2);
        
        organization.setUsers(users);
        assertNotNull(organization.getUsers());
        assertEquals(2, organization.getUsers().size());
        assertEquals(user1, organization.getUsers().get(0));
        assertEquals(user2, organization.getUsers().get(1));
    }

    @Test
    @DisplayName("Debería manejar lista de usuarios nula correctamente")
    void testUsers_Null() {
        organization.setUsers(null);
        assertNull(organization.getUsers());
    }

    @Test
    @DisplayName("Debería manejar lista de usuarios vacía correctamente")
    void testUsers_Empty() {
        organization.setUsers(new ArrayList<>());
        assertNotNull(organization.getUsers());
        assertTrue(organization.getUsers().isEmpty());
    }
}

