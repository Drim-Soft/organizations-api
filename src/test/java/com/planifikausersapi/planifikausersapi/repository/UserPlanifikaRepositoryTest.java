package com.planifikausersapi.planifikausersapi.repository;

import com.planifikausersapi.planifikausersapi.model.Organization;
import com.planifikausersapi.planifikausersapi.model.UserPlanifika;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class UserPlanifikaRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserPlanifikaRepository userPlanifikaRepository;

    @Test
    void testSaveUserPlanifika() {
        Organization organization = new Organization();
        organization.setNit("12345678-9");
        organization.setName("Empresa Test");
        entityManager.persistAndFlush(organization);

        UserPlanifika user = new UserPlanifika();
        user.setName("Juan Pérez");
        user.setPhotoURL("https://example.com/photo.jpg");
        user.setOrganization(organization);

        UserPlanifika savedUser = userPlanifikaRepository.save(user);

        assertNotNull(savedUser.getId());
        assertEquals("Juan Pérez", savedUser.getName());
        assertEquals("https://example.com/photo.jpg", savedUser.getPhotoURL());
        assertEquals(organization.getId(), savedUser.getOrganization().getId());
    }

    @Test
    void testFindById() {
        Organization organization = new Organization();
        organization.setNit("12345678-9");
        organization.setName("Empresa Test");
        entityManager.persistAndFlush(organization);

        UserPlanifika user = new UserPlanifika();
        user.setName("María García");
        user.setOrganization(organization);
        entityManager.persistAndFlush(user);

        Optional<UserPlanifika> found = userPlanifikaRepository.findById(user.getId());

        assertTrue(found.isPresent());
        assertEquals("María García", found.get().getName());
        assertEquals(organization.getId(), found.get().getOrganization().getId());
    }

    @Test
    void testFindByIdNotFound() {
        Optional<UserPlanifika> found = userPlanifikaRepository.findById(999L);
        assertFalse(found.isPresent());
    }

    @Test
    void testFindAll() {
        Organization organization = new Organization();
        organization.setNit("12345678-9");
        organization.setName("Empresa Test");
        entityManager.persistAndFlush(organization);

        UserPlanifika user1 = new UserPlanifika();
        user1.setName("Usuario 1");
        user1.setOrganization(organization);

        UserPlanifika user2 = new UserPlanifika();
        user2.setName("Usuario 2");
        user2.setOrganization(organization);

        entityManager.persistAndFlush(user1);
        entityManager.persistAndFlush(user2);

        List<UserPlanifika> users = userPlanifikaRepository.findAll();

        assertEquals(2, users.size());
        assertTrue(users.stream().anyMatch(user -> "Usuario 1".equals(user.getName())));
        assertTrue(users.stream().anyMatch(user -> "Usuario 2".equals(user.getName())));
    }

    @Test
    void testDeleteById() {
        Organization organization = new Organization();
        organization.setNit("12345678-9");
        organization.setName("Empresa Test");
        entityManager.persistAndFlush(organization);

        UserPlanifika user = new UserPlanifika();
        user.setName("Usuario a Eliminar");
        user.setOrganization(organization);
        entityManager.persistAndFlush(user);

        Long id = user.getId();
        userPlanifikaRepository.deleteById(id);

        Optional<UserPlanifika> found = userPlanifikaRepository.findById(id);
        assertFalse(found.isPresent());
    }

    @Test
    void testUpdateUserPlanifika() {
        Organization organization = new Organization();
        organization.setNit("12345678-9");
        organization.setName("Empresa Test");
        entityManager.persistAndFlush(organization);

        UserPlanifika user = new UserPlanifika();
        user.setName("Usuario Original");
        user.setOrganization(organization);
        entityManager.persistAndFlush(user);

        user.setName("Usuario Actualizado");
        user.setPhotoURL("https://example.com/new-photo.jpg");

        UserPlanifika updatedUser = userPlanifikaRepository.save(user);

        assertEquals(user.getId(), updatedUser.getId());
        assertEquals("Usuario Actualizado", updatedUser.getName());
        assertEquals("https://example.com/new-photo.jpg", updatedUser.getPhotoURL());
        assertEquals(organization.getId(), updatedUser.getOrganization().getId());
    }

    @Test
    void testFindAllEmpty() {
        List<UserPlanifika> users = userPlanifikaRepository.findAll();
        assertTrue(users.isEmpty());
    }

    @Test
    void testUserWithOrganizationRelationship() {
        Organization organization = new Organization();
        organization.setNit("12345678-9");
        organization.setName("Empresa Test");
        organization.setAddress("Calle Principal 123");
        entityManager.persistAndFlush(organization);

        UserPlanifika user = new UserPlanifika();
        user.setName("Usuario con Organización");
        user.setPhotoURL("https://example.com/user.jpg");
        user.setOrganization(organization);
        entityManager.persistAndFlush(user);

        Optional<UserPlanifika> found = userPlanifikaRepository.findById(user.getId());

        assertTrue(found.isPresent());
        assertNotNull(found.get().getOrganization());
        assertEquals("Empresa Test", found.get().getOrganization().getName());
        assertEquals("12345678-9", found.get().getOrganization().getNit());
        assertEquals("Calle Principal 123", found.get().getOrganization().getAddress());
    }
}
