package com.planifikausersapi.planifikausersapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.planifikausersapi.planifikausersapi.model.Organization;
import com.planifikausersapi.planifikausersapi.model.UserPlanifika;
import com.planifikausersapi.planifikausersapi.service.OrganizationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrganizationController.class)
class OrganizationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrganizationService organizationService;

    @Autowired
    private ObjectMapper objectMapper;

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
    void testGetAllOrganizations() throws Exception {
        List<Organization> organizations = Arrays.asList(organization);
        when(organizationService.findAll()).thenReturn(organizations);

        mockMvc.perform(get("/organizations"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Empresa Test"))
                .andExpect(jsonPath("$[0].nit").value("12345678-9"));
    }

    @Test
    void testGetOrganizationById() throws Exception {
        when(organizationService.findById(1L)).thenReturn(Optional.of(organization));

        mockMvc.perform(get("/organizations/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Empresa Test"))
                .andExpect(jsonPath("$.nit").value("12345678-9"))
                .andExpect(jsonPath("$.address").value("Calle 123 #45-67"))
                .andExpect(jsonPath("$.phone").value("+57 300 123 4567"));
    }

    @Test
    void testGetOrganizationByIdNotFound() throws Exception {
        when(organizationService.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/organizations/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreateOrganization() throws Exception {
        Organization newOrganization = new Organization();
        newOrganization.setNit("98765432-1");
        newOrganization.setName("Nueva Empresa");
        newOrganization.setAddress("Nueva Dirección");

        when(organizationService.save(any(Organization.class))).thenReturn(newOrganization);

        mockMvc.perform(post("/organizations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newOrganization)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Nueva Empresa"))
                .andExpect(jsonPath("$.nit").value("98765432-1"))
                .andExpect(jsonPath("$.address").value("Nueva Dirección"));
    }

    @Test
    void testUpdateOrganization() throws Exception {
        Organization updatedOrganization = new Organization();
        updatedOrganization.setId(1L);
        updatedOrganization.setNit("12345678-9");
        updatedOrganization.setName("Empresa Actualizada");
        updatedOrganization.setAddress("Dirección Actualizada");

        when(organizationService.findById(1L)).thenReturn(Optional.of(organization));
        when(organizationService.save(any(Organization.class))).thenReturn(updatedOrganization);

        mockMvc.perform(put("/organizations/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedOrganization)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Empresa Actualizada"))
                .andExpect(jsonPath("$.address").value("Dirección Actualizada"));
    }

    @Test
    void testUpdateOrganizationNotFound() throws Exception {
        Organization updatedOrganization = new Organization();
        updatedOrganization.setName("Empresa Actualizada");

        when(organizationService.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(put("/organizations/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedOrganization)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteOrganization() throws Exception {
        mockMvc.perform(delete("/organizations/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testGetUsersByOrganization() throws Exception {
        List<UserPlanifika> users = Arrays.asList(user);
        when(organizationService.getUsersByOrganization(1L)).thenReturn(users);

        mockMvc.perform(get("/organizations/1/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Juan Pérez"))
                .andExpect(jsonPath("$[0].photoURL").value("https://example.com/photo.jpg"));
    }

    @Test
    void testGetUsersByOrganizationEmpty() throws Exception {
        when(organizationService.getUsersByOrganization(1L)).thenReturn(Arrays.asList());

        mockMvc.perform(get("/organizations/1/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void testLinkUserToOrganization() throws Exception {
        UserPlanifika linkedUser = new UserPlanifika();
        linkedUser.setId(2L);
        linkedUser.setName("María García");
        linkedUser.setOrganization(organization);

        when(organizationService.linkUserToOrganization(1L, 2L)).thenReturn(Optional.of(linkedUser));

        mockMvc.perform(patch("/organizations/1/users/2"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.name").value("María García"));
    }

    @Test
    void testLinkUserToOrganizationNotFound() throws Exception {
        when(organizationService.linkUserToOrganization(999L, 999L)).thenReturn(Optional.empty());

        mockMvc.perform(patch("/organizations/999/users/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreateOrganizationWithInvalidData() throws Exception {
        Organization invalidOrganization = new Organization();
        // Missing required fields

        mockMvc.perform(post("/organizations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidOrganization)))
                .andExpect(status().isOk()); // The controller doesn't validate, it just passes to service
    }

    @Test
    void testCorsHeaders() throws Exception {
        when(organizationService.findAll()).thenReturn(Arrays.asList(organization));

        mockMvc.perform(get("/organizations")
                .header("Origin", "http://localhost:3000"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "*"));
    }
}
