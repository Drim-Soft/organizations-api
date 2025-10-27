package com.planifikausersapi.planifikausersapi;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.planifikausersapi.planifikausersapi.model.Organization;
import com.planifikausersapi.planifikausersapi.model.UserPlanifika;
import com.planifikausersapi.planifikausersapi.repository.OrganizationRepository;
import com.planifikausersapi.planifikausersapi.repository.UserPlanifikaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Transactional
class OrganizationIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private UserPlanifikaRepository userPlanifikaRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void testCompleteOrganizationWorkflow() throws Exception {
        // 1. Crear una organización
        Organization organization = new Organization();
        organization.setNit("12345678-9");
        organization.setName("Empresa de Prueba");
        organization.setAddress("Calle Principal 123");
        organization.setPhone("+57 300 123 4567");

        String organizationJson = objectMapper.writeValueAsString(organization);

        // Crear organización via API
        mockMvc.perform(post("/organizations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(organizationJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Empresa de Prueba"))
                .andExpect(jsonPath("$.nit").value("12345678-9"));

        // 2. Obtener todas las organizaciones
        mockMvc.perform(get("/organizations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        // 3. Obtener organización por ID
        Organization savedOrg = organizationRepository.findAll().get(0);
        Long orgId = savedOrg.getId();

        mockMvc.perform(get("/organizations/" + orgId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(orgId))
                .andExpect(jsonPath("$.name").value("Empresa de Prueba"));

        // 4. Crear un usuario
        UserPlanifika user = new UserPlanifika();
        user.setName("Usuario de Prueba");
        user.setPhotoURL("https://example.com/user.jpg");
        user.setOrganization(savedOrg);

        UserPlanifika savedUser = userPlanifikaRepository.save(user);

        // 5. Obtener usuarios de la organización
        mockMvc.perform(get("/organizations/" + orgId + "/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].name").value("Usuario de Prueba"));

        // 6. Actualizar organización
        organization.setId(orgId);
        organization.setName("Empresa Actualizada");
        organization.setAddress("Nueva Dirección");

        String updatedOrganizationJson = objectMapper.writeValueAsString(organization);

        mockMvc.perform(put("/organizations/" + orgId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updatedOrganizationJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Empresa Actualizada"))
                .andExpect(jsonPath("$.address").value("Nueva Dirección"));

        // 7. Verificar que la actualización se guardó
        mockMvc.perform(get("/organizations/" + orgId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Empresa Actualizada"))
                .andExpect(jsonPath("$.address").value("Nueva Dirección"));

        // 8. Eliminar organización
        mockMvc.perform(delete("/organizations/" + orgId))
                .andExpect(status().isNoContent());

        // 9. Verificar que la organización fue eliminada
        mockMvc.perform(get("/organizations/" + orgId))
                .andExpect(status().isNotFound());
    }

    @Test
    void testLinkUserToOrganizationWorkflow() throws Exception {
        // 1. Crear organización
        Organization organization = new Organization();
        organization.setNit("98765432-1");
        organization.setName("Empresa para Usuario");
        organization.setAddress("Calle Usuario 456");

        Organization savedOrg = organizationRepository.save(organization);

        // 2. Crear usuario sin organización
        UserPlanifika user = new UserPlanifika();
        user.setName("Usuario Sin Organización");
        user.setPhotoURL("https://example.com/user.jpg");
        user.setOrganization(savedOrg); // Asignar organización temporalmente para evitar constraint

        UserPlanifika savedUser = userPlanifikaRepository.save(user);
        
        // Desasociar temporalmente para la prueba
        savedUser.setOrganization(null);
        savedUser = userPlanifikaRepository.save(savedUser);

        // 3. Vincular usuario a organización
        mockMvc.perform(patch("/organizations/" + savedOrg.getId() + "/users/" + savedUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Usuario Sin Organización"));

        // 4. Verificar que el usuario está vinculado
        mockMvc.perform(get("/organizations/" + savedOrg.getId() + "/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void testErrorHandling() throws Exception {
        // Intentar obtener organización inexistente
        mockMvc.perform(get("/organizations/999"))
                .andExpect(status().isNotFound());

        // Intentar actualizar organización inexistente
        Organization organization = new Organization();
        organization.setName("Organización Inexistente");

        mockMvc.perform(put("/organizations/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(organization)))
                .andExpect(status().isNotFound());

        // Intentar vincular usuario inexistente
        mockMvc.perform(patch("/organizations/999/users/999"))
                .andExpect(status().isNotFound());
    }
}
