package com.planifikausersapi.planifikausersapi.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.planifikausersapi.planifikausersapi.model.Organization;
import com.planifikausersapi.planifikausersapi.model.UserPlanifika;
import com.planifikausersapi.planifikausersapi.service.OrganizationService;

import static org.mockito.Mockito.mock;

@WebMvcTest(OrganizationController.class)
@ContextConfiguration(classes = {OrganizationController.class, OrganizationControllerTest.TestConfig.class})
@DisplayName("Pruebas de integración para OrganizationController")
class OrganizationControllerTest {

    @TestConfiguration
    static class TestConfig {
        @Bean
        public OrganizationService organizationService() {
            return mock(OrganizationService.class);
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private ObjectMapper objectMapper;

    private Organization organization1;
    private Organization organization2;
    private UserPlanifika user1;
    private UserPlanifika user2;

    @BeforeEach
    void setUp() {
        // Resetear el mock antes de cada prueba
        reset(organizationService);
        
        // Configurar organización 1
        organization1 = new Organization();
        organization1.setId(1L);
        organization1.setNit("123456789");
        organization1.setName("Organización Test 1");
        organization1.setAddress("Calle Test 123");
        organization1.setPhone("1234567890");
        organization1.setPhotoURL("http://example.com/photo1.jpg");
        organization1.setDomain("test1.com");

        // Configurar organización 2
        organization2 = new Organization();
        organization2.setId(2L);
        organization2.setNit("987654321");
        organization2.setName("Organización Test 2");
        organization2.setAddress("Avenida Test 456");
        organization2.setPhone("0987654321");
        organization2.setPhotoURL("http://example.com/photo2.jpg");
        organization2.setDomain("test2.com");

        // Configurar usuarios
        user1 = new UserPlanifika();
        user1.setId(1L);
        user1.setName("Usuario 1");
        user1.setPhotoURL("http://example.com/user1.jpg");
        user1.setOrganization(organization1);

        user2 = new UserPlanifika();
        user2.setId(2L);
        user2.setName("Usuario 2");
        user2.setPhotoURL("http://example.com/user2.jpg");
        user2.setOrganization(organization1);
    }

    @Test
    @DisplayName("GET /organizations - Debería retornar todas las organizaciones")
    void testGetAll() throws Exception {
        // Arrange
        List<Organization> organizations = Arrays.asList(organization1, organization2);
        when(organizationService.findAll()).thenReturn(organizations);

        // Act & Assert
        mockMvc.perform(get("/organizations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Organización Test 1"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].name").value("Organización Test 2"));

        verify(organizationService, times(1)).findAll();
    }

    @Test
    @DisplayName("GET /organizations/paginated - Debería retornar página de organizaciones sin búsqueda")
    void testGetAllPaginated_WithoutSearch() throws Exception {
        // Arrange
        int page = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(page, size);
        List<Organization> organizations = Arrays.asList(organization1, organization2);
        Page<Organization> pageResult = new PageImpl<>(organizations, pageable, organizations.size());
        when(organizationService.findAllPaginated(page, size)).thenReturn(pageResult);

        // Act & Assert
        mockMvc.perform(get("/organizations/paginated")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.number").value(0))
                .andExpect(jsonPath("$.size").value(10));

        verify(organizationService, atLeastOnce()).findAllPaginated(page, size);
        verify(organizationService, never()).findAllPaginated(anyInt(), anyInt(), anyString());
    }

    @Test
    @DisplayName("GET /organizations/paginated - Debería retornar página filtrada cuando se proporciona search")
    void testGetAllPaginated_WithSearch() throws Exception {
        // Arrange
        int page = 0;
        int size = 10;
        String search = "Test 1";
        Pageable pageable = PageRequest.of(page, size);
        List<Organization> filteredOrganizations = Arrays.asList(organization1);
        Page<Organization> pageResult = new PageImpl<>(filteredOrganizations, pageable, 1);
        when(organizationService.findAllPaginated(page, size, search)).thenReturn(pageResult);

        // Act & Assert
        mockMvc.perform(get("/organizations/paginated")
                .param("page", "0")
                .param("size", "10")
                .param("search", search))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].name").value("Organización Test 1"))
                .andExpect(jsonPath("$.totalElements").value(1));

        verify(organizationService, times(1)).findAllPaginated(page, size, search);
    }

    @Test
    @DisplayName("GET /organizations/paginated - Debería usar findAllPaginated sin search cuando search está vacío")
    void testGetAllPaginated_WithEmptySearch() throws Exception {
        // Arrange
        int page = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(page, size);
        List<Organization> organizations = Arrays.asList(organization1, organization2);
        Page<Organization> pageResult = new PageImpl<>(organizations, pageable, organizations.size());
        when(organizationService.findAllPaginated(page, size)).thenReturn(pageResult);

        // Act & Assert
        mockMvc.perform(get("/organizations/paginated")
                .param("page", "0")
                .param("size", "10")
                .param("search", "   "))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2));

        verify(organizationService, times(1)).findAllPaginated(page, size);
        verify(organizationService, never()).findAllPaginated(anyInt(), anyInt(), anyString());
    }

    @Test
    @DisplayName("GET /organizations/paginated - Debería corregir página negativa a 0")
    void testGetAllPaginated_WithNegativePage() throws Exception {
        // Arrange
        int correctedPage = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(correctedPage, size);
        List<Organization> organizations = Arrays.asList(organization1, organization2);
        Page<Organization> pageResult = new PageImpl<>(organizations, pageable, organizations.size());
        when(organizationService.findAllPaginated(correctedPage, size)).thenReturn(pageResult);

        // Act & Assert
        mockMvc.perform(get("/organizations/paginated")
                .param("page", "-1")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.number").value(0));

        verify(organizationService, atLeastOnce()).findAllPaginated(correctedPage, size);
    }

    @Test
    @DisplayName("GET /organizations/paginated - Debería corregir size menor a 1 a 10")
    void testGetAllPaginated_WithInvalidSize() throws Exception {
        // Arrange
        int page = 0;
        int correctedSize = 10;
        Pageable pageable = PageRequest.of(page, correctedSize);
        List<Organization> organizations = Arrays.asList(organization1, organization2);
        Page<Organization> pageResult = new PageImpl<>(organizations, pageable, organizations.size());
        when(organizationService.findAllPaginated(page, correctedSize)).thenReturn(pageResult);

        // Act & Assert
        mockMvc.perform(get("/organizations/paginated")
                .param("page", "0")
                .param("size", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.size").value(10));

        verify(organizationService, atLeastOnce()).findAllPaginated(page, correctedSize);
    }

    @Test
    @DisplayName("GET /organizations/{id} - Debería retornar organización cuando existe")
    void testGetById_Success() throws Exception {
        // Arrange
        Long id = 1L;
        when(organizationService.findById(id)).thenReturn(Optional.of(organization1));

        // Act & Assert
        mockMvc.perform(get("/organizations/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Organización Test 1"))
                .andExpect(jsonPath("$.nit").value("123456789"));

        verify(organizationService, times(1)).findById(id);
    }

    @Test
    @DisplayName("GET /organizations/{id} - Debería retornar 404 cuando no existe")
    void testGetById_NotFound() throws Exception {
        // Arrange
        Long id = 999L;
        when(organizationService.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/organizations/{id}", id))
                .andExpect(status().isNotFound());

        verify(organizationService, times(1)).findById(id);
    }

    @Test
    @DisplayName("POST /organizations - Debería crear una nueva organización")
    void testCreate() throws Exception {
        // Arrange
        Organization newOrganization = new Organization();
        newOrganization.setNit("111222333");
        newOrganization.setName("Nueva Organización");
        newOrganization.setAddress("Nueva Dirección");
        newOrganization.setPhone("1111111111");
        newOrganization.setDomain("nueva.com");

        Organization savedOrganization = new Organization();
        savedOrganization.setId(3L);
        savedOrganization.setNit(newOrganization.getNit());
        savedOrganization.setName(newOrganization.getName());
        savedOrganization.setAddress(newOrganization.getAddress());
        savedOrganization.setPhone(newOrganization.getPhone());
        savedOrganization.setDomain(newOrganization.getDomain());

        when(organizationService.save(any(Organization.class))).thenReturn(savedOrganization);

        // Act & Assert
        mockMvc.perform(post("/organizations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newOrganization)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(3L))
                .andExpect(jsonPath("$.name").value("Nueva Organización"))
                .andExpect(jsonPath("$.nit").value("111222333"));

        verify(organizationService, times(1)).save(any(Organization.class));
    }

    @Test
    @DisplayName("PUT /organizations/{id} - Debería actualizar organización cuando existe")
    void testUpdate_Success() throws Exception {
        // Arrange
        Long id = 1L;
        Organization updatedOrganization = new Organization();
        updatedOrganization.setName("Organización Actualizada");
        updatedOrganization.setAddress("Dirección Actualizada");
        updatedOrganization.setNit("123456789");
        updatedOrganization.setPhone("1234567890");
        updatedOrganization.setDomain("test1.com");

        Organization savedOrganization = new Organization();
        savedOrganization.setId(id);
        savedOrganization.setName(updatedOrganization.getName());
        savedOrganization.setAddress(updatedOrganization.getAddress());
        savedOrganization.setNit(updatedOrganization.getNit());
        savedOrganization.setPhone(updatedOrganization.getPhone());
        savedOrganization.setDomain(updatedOrganization.getDomain());

        when(organizationService.findById(id)).thenReturn(Optional.of(organization1));
        when(organizationService.save(any(Organization.class))).thenReturn(savedOrganization);

        // Act & Assert
        mockMvc.perform(put("/organizations/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedOrganization)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value("Organización Actualizada"))
                .andExpect(jsonPath("$.address").value("Dirección Actualizada"));

        verify(organizationService, atLeastOnce()).findById(id);
        verify(organizationService, times(1)).save(any(Organization.class));
    }

    @Test
    @DisplayName("PUT /organizations/{id} - Debería retornar 404 cuando la organización no existe")
    void testUpdate_NotFound() throws Exception {
        // Arrange
        Long id = 999L;
        Organization updatedOrganization = new Organization();
        updatedOrganization.setName("Organización Actualizada");

        when(organizationService.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(put("/organizations/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedOrganization)))
                .andExpect(status().isNotFound());

        verify(organizationService, atLeastOnce()).findById(id);
        verify(organizationService, never()).save(any(Organization.class));
    }

    @Test
    @DisplayName("DELETE /organizations/{id} - Debería eliminar organización")
    void testDelete() throws Exception {
        // Arrange
        Long id = 1L;
        doNothing().when(organizationService).deleteById(id);

        // Act & Assert
        mockMvc.perform(delete("/organizations/{id}", id))
                .andExpect(status().isNoContent());

        verify(organizationService, times(1)).deleteById(id);
    }

    @Test
    @DisplayName("GET /organizations/{id}/users - Debería retornar lista de usuarios de la organización")
    void testGetUsersByOrganization() throws Exception {
        // Arrange
        Long id = 1L;
        List<UserPlanifika> users = Arrays.asList(user1, user2);
        when(organizationService.getUsersByOrganization(id)).thenReturn(users);

        // Act & Assert
        mockMvc.perform(get("/organizations/{id}/users", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Usuario 1"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].name").value("Usuario 2"));

        verify(organizationService, times(1)).getUsersByOrganization(id);
    }

    @Test
    @DisplayName("GET /organizations/{id}/users - Debería retornar lista vacía cuando no hay usuarios")
    void testGetUsersByOrganization_EmptyList() throws Exception {
        // Arrange
        Long id = 2L;
        when(organizationService.getUsersByOrganization(id)).thenReturn(Arrays.asList());

        // Act & Assert
        mockMvc.perform(get("/organizations/{id}/users", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));

        verify(organizationService, times(1)).getUsersByOrganization(id);
    }
}

