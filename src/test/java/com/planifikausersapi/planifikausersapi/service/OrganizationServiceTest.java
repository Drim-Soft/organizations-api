package com.planifikausersapi.planifikausersapi.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.planifikausersapi.planifikausersapi.model.Organization;
import com.planifikausersapi.planifikausersapi.model.UserPlanifika;
import com.planifikausersapi.planifikausersapi.repository.OrganizationRepository;
import com.planifikausersapi.planifikausersapi.repository.UserPlanifikaRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas unitarias de OrganizationService")
class OrganizationServiceTest {

    @Mock
    private OrganizationRepository organizationRepository;

    @Mock
    private UserPlanifikaRepository userPlanifikaRepository;

    @InjectMocks
    private OrganizationService organizationService;

    private Organization organization1;
    private Organization organization2;
    private UserPlanifika user1;
    private UserPlanifika user2;

    @BeforeEach
    void setUp() {
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
    @DisplayName("Debería retornar todas las organizaciones cuando se llama a findAll()")
    void testFindAll() {
        // Arrange
        List<Organization> expectedOrganizations = Arrays.asList(organization1, organization2);
        when(organizationRepository.findAll()).thenReturn(expectedOrganizations);

        // Act
        List<Organization> result = organizationService.findAll();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(expectedOrganizations, result);
        verify(organizationRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debería retornar una lista vacía cuando no hay organizaciones")
    void testFindAll_EmptyList() {
        // Arrange
        when(organizationRepository.findAll()).thenReturn(new ArrayList<>());

        // Act
        List<Organization> result = organizationService.findAll();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(organizationRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debería retornar una página de organizaciones cuando se llama a findAllPaginated con page y size")
    void testFindAllPaginated() {
        // Arrange
        int page = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(page, size);
        List<Organization> organizations = Arrays.asList(organization1, organization2);
        Page<Organization> expectedPage = new PageImpl<>(organizations, pageable, organizations.size());
        when(organizationRepository.findAll(pageable)).thenReturn(expectedPage);

        // Act
        Page<Organization> result = organizationService.findAllPaginated(page, size);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals(2, result.getTotalElements());
        assertEquals(0, result.getNumber());
        assertEquals(10, result.getSize());
        verify(organizationRepository, times(1)).findAll(pageable);
    }

    @Test
    @DisplayName("Debería retornar una página vacía cuando no hay organizaciones")
    void testFindAllPaginated_EmptyPage() {
        // Arrange
        int page = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(page, size);
        Page<Organization> emptyPage = new PageImpl<>(new ArrayList<>(), pageable, 0);
        when(organizationRepository.findAll(pageable)).thenReturn(emptyPage);

        // Act
        Page<Organization> result = organizationService.findAllPaginated(page, size);

        // Assert
        assertNotNull(result);
        assertTrue(result.getContent().isEmpty());
        assertEquals(0, result.getTotalElements());
        verify(organizationRepository, times(1)).findAll(pageable);
    }

    @Test
    @DisplayName("Debería retornar una página de organizaciones filtradas cuando se proporciona un término de búsqueda")
    void testFindAllPaginated_WithSearch() {
        // Arrange
        int page = 0;
        int size = 10;
        String search = "Test 1";
        Pageable pageable = PageRequest.of(page, size);
        List<Organization> filteredOrganizations = Arrays.asList(organization1);
        Page<Organization> expectedPage = new PageImpl<>(filteredOrganizations, pageable, 1);
        when(organizationRepository.findBySearchTerm(search.trim(), pageable)).thenReturn(expectedPage);

        // Act
        Page<Organization> result = organizationService.findAllPaginated(page, size, search);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(organization1, result.getContent().get(0));
        verify(organizationRepository, times(1)).findBySearchTerm(search.trim(), pageable);
        verify(organizationRepository, never()).findAll(pageable);
    }

    @Test
    @DisplayName("Debería retornar todas las organizaciones cuando el término de búsqueda es null")
    void testFindAllPaginated_WithNullSearch() {
        // Arrange
        int page = 0;
        int size = 10;
        String search = null;
        Pageable pageable = PageRequest.of(page, size);
        List<Organization> organizations = Arrays.asList(organization1, organization2);
        Page<Organization> expectedPage = new PageImpl<>(organizations, pageable, organizations.size());
        when(organizationRepository.findAll(pageable)).thenReturn(expectedPage);

        // Act
        Page<Organization> result = organizationService.findAllPaginated(page, size, search);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        verify(organizationRepository, times(1)).findAll(pageable);
        verify(organizationRepository, never()).findBySearchTerm(anyString(), any(Pageable.class));
    }

    @Test
    @DisplayName("Debería retornar todas las organizaciones cuando el término de búsqueda está vacío")
    void testFindAllPaginated_WithEmptySearch() {
        // Arrange
        int page = 0;
        int size = 10;
        String search = "   ";
        Pageable pageable = PageRequest.of(page, size);
        List<Organization> organizations = Arrays.asList(organization1, organization2);
        Page<Organization> expectedPage = new PageImpl<>(organizations, pageable, organizations.size());
        when(organizationRepository.findAll(pageable)).thenReturn(expectedPage);

        // Act
        Page<Organization> result = organizationService.findAllPaginated(page, size, search);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        verify(organizationRepository, times(1)).findAll(pageable);
        verify(organizationRepository, never()).findBySearchTerm(anyString(), any(Pageable.class));
    }

    @Test
    @DisplayName("Debería retornar una organización cuando se encuentra por ID")
    void testFindById_Success() {
        // Arrange
        Long id = 1L;
        when(organizationRepository.findById(id)).thenReturn(Optional.of(organization1));

        // Act
        Optional<Organization> result = organizationService.findById(id);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(organization1, result.get());
        assertEquals(id, result.get().getId());
        verify(organizationRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("Debería retornar Optional vacío cuando no se encuentra la organización por ID")
    void testFindById_NotFound() {
        // Arrange
        Long id = 999L;
        when(organizationRepository.findById(id)).thenReturn(Optional.empty());

        // Act
        Optional<Organization> result = organizationService.findById(id);

        // Assert
        assertFalse(result.isPresent());
        verify(organizationRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("Debería guardar y retornar una organización cuando se llama a save()")
    void testSave() {
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

        when(organizationRepository.save(newOrganization)).thenReturn(savedOrganization);

        // Act
        Organization result = organizationService.save(newOrganization);

        // Assert
        assertNotNull(result);
        assertEquals(3L, result.getId());
        assertEquals(newOrganization.getNit(), result.getNit());
        assertEquals(newOrganization.getName(), result.getName());
        verify(organizationRepository, times(1)).save(newOrganization);
    }

    @Test
    @DisplayName("Debería actualizar una organización existente cuando se llama a save()")
    void testSave_Update() {
        // Arrange
        organization1.setName("Nombre Actualizado");
        organization1.setAddress("Dirección Actualizada");
        when(organizationRepository.save(organization1)).thenReturn(organization1);

        // Act
        Organization result = organizationService.save(organization1);

        // Assert
        assertNotNull(result);
        assertEquals("Nombre Actualizado", result.getName());
        assertEquals("Dirección Actualizada", result.getAddress());
        verify(organizationRepository, times(1)).save(organization1);
    }

    @Test
    @DisplayName("Debería eliminar una organización cuando se llama a deleteById()")
    void testDeleteById() {
        // Arrange
        Long id = 1L;
        doNothing().when(organizationRepository).deleteById(id);

        // Act
        organizationService.deleteById(id);

        // Assert
        verify(organizationRepository, times(1)).deleteById(id);
    }

    @Test
    @DisplayName("Debería retornar una lista de usuarios cuando se llama a getUsersByOrganization()")
    void testGetUsersByOrganization() {
        // Arrange
        Long organizationId = 1L;
        List<UserPlanifika> expectedUsers = Arrays.asList(user1, user2);
        when(userPlanifikaRepository.findByOrganization_Id(organizationId)).thenReturn(expectedUsers);

        // Act
        List<UserPlanifika> result = organizationService.getUsersByOrganization(organizationId);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(expectedUsers, result);
        verify(userPlanifikaRepository, times(1)).findByOrganization_Id(organizationId);
    }

    @Test
    @DisplayName("Debería retornar una lista vacía cuando la organización no tiene usuarios")
    void testGetUsersByOrganization_EmptyList() {
        // Arrange
        Long organizationId = 2L;
        when(userPlanifikaRepository.findByOrganization_Id(organizationId)).thenReturn(new ArrayList<>());

        // Act
        List<UserPlanifika> result = organizationService.getUsersByOrganization(organizationId);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(userPlanifikaRepository, times(1)).findByOrganization_Id(organizationId);
    }

    @Test
    @DisplayName("Debería recortar espacios en blanco del término de búsqueda")
    void testFindAllPaginated_TrimSearch() {
        // Arrange
        int page = 0;
        int size = 10;
        String search = "  Test 1  ";
        String trimmedSearch = "Test 1";
        Pageable pageable = PageRequest.of(page, size);
        List<Organization> filteredOrganizations = Arrays.asList(organization1);
        Page<Organization> expectedPage = new PageImpl<>(filteredOrganizations, pageable, 1);
        when(organizationRepository.findBySearchTerm(trimmedSearch, pageable)).thenReturn(expectedPage);

        // Act
        Page<Organization> result = organizationService.findAllPaginated(page, size, search);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(organizationRepository, times(1)).findBySearchTerm(trimmedSearch, pageable);
    }
}

