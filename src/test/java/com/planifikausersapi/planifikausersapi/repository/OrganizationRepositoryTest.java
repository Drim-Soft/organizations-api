package com.planifikausersapi.planifikausersapi.repository;

import com.planifikausersapi.planifikausersapi.model.Organization;
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
class OrganizationRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Test
    void testSaveOrganization() {
        Organization organization = new Organization();
        organization.setNit("12345678-9");
        organization.setName("Empresa Test");
        organization.setAddress("Calle 123 #45-67");
        organization.setPhone("+57 300 123 4567");

        Organization savedOrganization = organizationRepository.save(organization);

        assertNotNull(savedOrganization.getId());
        assertEquals("12345678-9", savedOrganization.getNit());
        assertEquals("Empresa Test", savedOrganization.getName());
        assertEquals("Calle 123 #45-67", savedOrganization.getAddress());
        assertEquals("+57 300 123 4567", savedOrganization.getPhone());
    }

    @Test
    void testFindById() {
        Organization organization = new Organization();
        organization.setNit("12345678-9");
        organization.setName("Empresa Test");

        entityManager.persistAndFlush(organization);

        Optional<Organization> found = organizationRepository.findById(organization.getId());

        assertTrue(found.isPresent());
        assertEquals("12345678-9", found.get().getNit());
        assertEquals("Empresa Test", found.get().getName());
    }

    @Test
    void testFindByIdNotFound() {
        Optional<Organization> found = organizationRepository.findById(999L);
        assertFalse(found.isPresent());
    }

    @Test
    void testFindAll() {
        Organization organization1 = new Organization();
        organization1.setNit("12345678-9");
        organization1.setName("Empresa 1");

        Organization organization2 = new Organization();
        organization2.setNit("98765432-1");
        organization2.setName("Empresa 2");

        entityManager.persistAndFlush(organization1);
        entityManager.persistAndFlush(organization2);

        List<Organization> organizations = organizationRepository.findAll();

        assertEquals(2, organizations.size());
        assertTrue(organizations.stream().anyMatch(org -> "Empresa 1".equals(org.getName())));
        assertTrue(organizations.stream().anyMatch(org -> "Empresa 2".equals(org.getName())));
    }

    @Test
    void testDeleteById() {
        Organization organization = new Organization();
        organization.setNit("12345678-9");
        organization.setName("Empresa Test");

        entityManager.persistAndFlush(organization);
        Long id = organization.getId();

        organizationRepository.deleteById(id);

        Optional<Organization> found = organizationRepository.findById(id);
        assertFalse(found.isPresent());
    }

    @Test
    void testUpdateOrganization() {
        Organization organization = new Organization();
        organization.setNit("12345678-9");
        organization.setName("Empresa Original");

        entityManager.persistAndFlush(organization);

        organization.setName("Empresa Actualizada");
        organization.setAddress("Nueva Dirección");

        Organization updatedOrganization = organizationRepository.save(organization);

        assertEquals(organization.getId(), updatedOrganization.getId());
        assertEquals("Empresa Actualizada", updatedOrganization.getName());
        assertEquals("Nueva Dirección", updatedOrganization.getAddress());
        assertEquals("12345678-9", updatedOrganization.getNit());
    }

    @Test
    void testFindAllEmpty() {
        List<Organization> organizations = organizationRepository.findAll();
        assertTrue(organizations.isEmpty());
    }
}
