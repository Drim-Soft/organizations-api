package com.planifikausersapi.planifikausersapi.service;

import com.planifikausersapi.planifikausersapi.model.Organization;
import com.planifikausersapi.planifikausersapi.model.UserPlanifika;
import com.planifikausersapi.planifikausersapi.repository.OrganizationRepository;
import com.planifikausersapi.planifikausersapi.repository.UserPlanifikaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrganizationServiceTest {

    @Mock
    private OrganizationRepository organizationRepository;

    @Mock
    private UserPlanifikaRepository userPlanifikaRepository;

    @InjectMocks
    private OrganizationService organizationService;

    private Organization organization;
    private UserPlanifika user;

    @BeforeEach
    void setUp() {
        organization = new Organization();
        organization.setId(1L);
        organization.setNit("12345678-9");
        organization.setName("Empresa Test");
        organization.setAddress("Calle 123 #45-67");
        organization.setPhone("+57 300 123 4567");

        user = new UserPlanifika();
        user.setId(1L);
        user.setName("Juan Pérez");
        user.setPhotoURL("https://example.com/photo.jpg");
        user.setOrganization(organization);
    }

    @Test
    void testFindAll() {
        List<Organization> organizations = Arrays.asList(organization);
        when(organizationRepository.findAll()).thenReturn(organizations);

        List<Organization> result = organizationService.findAll();

        assertEquals(1, result.size());
        assertEquals("Empresa Test", result.get(0).getName());
        verify(organizationRepository).findAll();
    }

    @Test
    void testFindById() {
        when(organizationRepository.findById(1L)).thenReturn(Optional.of(organization));

        Optional<Organization> result = organizationService.findById(1L);

        assertTrue(result.isPresent());
        assertEquals("Empresa Test", result.get().getName());
        verify(organizationRepository).findById(1L);
    }

    @Test
    void testFindByIdNotFound() {
        when(organizationRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<Organization> result = organizationService.findById(999L);

        assertFalse(result.isPresent());
        verify(organizationRepository).findById(999L);
    }

    @Test
    void testSave() {
        Organization newOrganization = new Organization();
        newOrganization.setNit("98765432-1");
        newOrganization.setName("Nueva Empresa");

        when(organizationRepository.save(any(Organization.class))).thenReturn(newOrganization);

        Organization result = organizationService.save(newOrganization);

        assertEquals("Nueva Empresa", result.getName());
        assertEquals("98765432-1", result.getNit());
        verify(organizationRepository).save(newOrganization);
    }

    @Test
    void testDeleteById() {
        doNothing().when(organizationRepository).deleteById(1L);

        organizationService.deleteById(1L);

        verify(organizationRepository).deleteById(1L);
    }

    @Test
    void testGetUsersByOrganization() {
        List<UserPlanifika> users = Arrays.asList(user);
        organization.setUsers(users);

        when(organizationRepository.findById(1L)).thenReturn(Optional.of(organization));

        List<UserPlanifika> result = organizationService.getUsersByOrganization(1L);

        assertEquals(1, result.size());
        assertEquals("Juan Pérez", result.get(0).getName());
        verify(organizationRepository).findById(1L);
    }

    @Test
    void testGetUsersByOrganizationNotFound() {
        when(organizationRepository.findById(999L)).thenReturn(Optional.empty());

        List<UserPlanifika> result = organizationService.getUsersByOrganization(999L);

        assertTrue(result.isEmpty());
        verify(organizationRepository).findById(999L);
    }

    @Test
    void testGetUsersByOrganizationWithNoUsers() {
        organization.setUsers(Arrays.asList());
        when(organizationRepository.findById(1L)).thenReturn(Optional.of(organization));

        List<UserPlanifika> result = organizationService.getUsersByOrganization(1L);

        assertTrue(result.isEmpty());
        verify(organizationRepository).findById(1L);
    }

    @Test
    void testLinkUserToOrganization() {
        UserPlanifika userToLink = new UserPlanifika();
        userToLink.setId(2L);
        userToLink.setName("María García");
        userToLink.setOrganization(null);

        when(organizationRepository.findById(1L)).thenReturn(Optional.of(organization));
        when(userPlanifikaRepository.findById(2L)).thenReturn(Optional.of(userToLink));
        when(userPlanifikaRepository.save(any(UserPlanifika.class))).thenReturn(userToLink);

        Optional<UserPlanifika> result = organizationService.linkUserToOrganization(1L, 2L);

        assertTrue(result.isPresent());
        assertEquals(organization, result.get().getOrganization());
        verify(organizationRepository).findById(1L);
        verify(userPlanifikaRepository).findById(2L);
        verify(userPlanifikaRepository).save(userToLink);
    }

    @Test
    void testLinkUserToOrganizationNotFound() {
        when(organizationRepository.findById(999L)).thenReturn(Optional.empty());
        when(userPlanifikaRepository.findById(1L)).thenReturn(Optional.of(user));

        Optional<UserPlanifika> result = organizationService.linkUserToOrganization(999L, 1L);

        assertFalse(result.isPresent());
        verify(organizationRepository).findById(999L);
        verify(userPlanifikaRepository).findById(1L);
        verify(userPlanifikaRepository, never()).save(any(UserPlanifika.class));
    }

    @Test
    void testLinkUserToOrganizationUserNotFound() {
        when(organizationRepository.findById(1L)).thenReturn(Optional.of(organization));
        when(userPlanifikaRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<UserPlanifika> result = organizationService.linkUserToOrganization(1L, 999L);

        assertFalse(result.isPresent());
        verify(organizationRepository).findById(1L);
        verify(userPlanifikaRepository).findById(999L);
        verify(userPlanifikaRepository, never()).save(any(UserPlanifika.class));
    }

    @Test
    void testLinkUserToOrganizationBothNotFound() {
        when(organizationRepository.findById(999L)).thenReturn(Optional.empty());
        when(userPlanifikaRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<UserPlanifika> result = organizationService.linkUserToOrganization(999L, 999L);

        assertFalse(result.isPresent());
        verify(organizationRepository).findById(999L);
        verify(userPlanifikaRepository).findById(999L);
        verify(userPlanifikaRepository, never()).save(any(UserPlanifika.class));
    }

    @Test
    void testLinkUserToOrganizationSuccess() {
        UserPlanifika userToLink = new UserPlanifika();
        userToLink.setId(2L);
        userToLink.setName("María García");

        when(organizationRepository.findById(1L)).thenReturn(Optional.of(organization));
        when(userPlanifikaRepository.findById(2L)).thenReturn(Optional.of(userToLink));
        when(userPlanifikaRepository.save(any(UserPlanifika.class))).thenAnswer(invocation -> {
            UserPlanifika user = invocation.getArgument(0);
            user.setOrganization(organization);
            return user;
        });

        Optional<UserPlanifika> result = organizationService.linkUserToOrganization(1L, 2L);

        assertTrue(result.isPresent());
        assertEquals(organization, result.get().getOrganization());
        assertEquals("María García", result.get().getName());
        verify(organizationRepository).findById(1L);
        verify(userPlanifikaRepository).findById(2L);
        verify(userPlanifikaRepository).save(userToLink);
    }
}
