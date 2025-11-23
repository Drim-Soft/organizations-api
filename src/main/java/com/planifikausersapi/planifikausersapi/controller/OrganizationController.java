package com.planifikausersapi.planifikausersapi.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.planifikausersapi.planifikausersapi.model.Organization;
import com.planifikausersapi.planifikausersapi.model.UserPlanifika;
import com.planifikausersapi.planifikausersapi.service.OrganizationService;

import java.util.List;

/**
 * Controlador REST para gestionar organizaciones.
 * Proporciona endpoints para operaciones CRUD y consultas relacionadas.
 */
@RestController
@RequestMapping("/organizations")
public class OrganizationController {

	private static final int DEFAULT_PAGE_SIZE = 10;
	private static final int DEFAULT_PAGE_NUMBER = 0;

	private final OrganizationService organizationService;

	/**
	 * Constructor para inyección de dependencias.
	 *
	 * @param organizationService servicio de organizaciones
	 */
	public OrganizationController(OrganizationService organizationService) {
		this.organizationService = organizationService;
	}

	/**
	 * Obtiene todas las organizaciones.
	 *
	 * @return lista de todas las organizaciones
	 */
	@GetMapping
	public List<Organization> getAll() {
		return organizationService.findAll();
	}

	/**
	 * Obtiene organizaciones paginadas con opción de búsqueda.
	 *
	 * @param page número de página (inicia en 0)
	 * @param size tamaño de la página
	 * @param search término de búsqueda opcional
	 * @return página de organizaciones
	 */
	@GetMapping("/paginated")
	public Page<Organization> getAllPaginated(
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size,
			@RequestParam(required = false) String search) {
		// Validar parámetros de paginación
		if (page < DEFAULT_PAGE_NUMBER) {
			page = DEFAULT_PAGE_NUMBER;
		}
		if (size < 1) {
			size = DEFAULT_PAGE_SIZE;
		}
		if (search != null && !search.trim().isEmpty()) {
			return organizationService.findAllPaginated(page, size, search);
		}
		return organizationService.findAllPaginated(page, size);
	}

	/**
	 * Obtiene una organización por su ID.
	 *
	 * @param id identificador de la organización
	 * @return ResponseEntity con la organización o 404 si no existe
	 */
	@GetMapping("/{id}")
	public ResponseEntity<Organization> getById(@PathVariable Long id) {
		return organizationService.findById(id)
				.map(ResponseEntity::ok)
				.orElse(ResponseEntity.notFound().build());
	}

	/**
	 * Crea una nueva organización.
	 *
	 * @param organization organización a crear
	 * @return organización creada
	 */
	@PostMapping
	public Organization create(@RequestBody Organization organization) {
		return organizationService.save(organization);
	}

	/**
	 * Actualiza una organización existente.
	 *
	 * @param id identificador de la organización
	 * @param organization datos actualizados de la organización
	 * @return ResponseEntity con la organización actualizada o 404 si no existe
	 */
	@PutMapping("/{id}")
	public ResponseEntity<Organization> update(
			@PathVariable Long id,
			@RequestBody Organization organization) {
		return organizationService.findById(id)
				.map(ignored -> {
					organization.setId(id);
					return ResponseEntity.ok(organizationService.save(organization));
				})
				.orElse(ResponseEntity.notFound().build());
	}

	/**
	 * Elimina una organización por su ID.
	 *
	 * @param id identificador de la organización
	 * @return ResponseEntity sin contenido (204)
	 */
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		organizationService.deleteById(id);
		return ResponseEntity.noContent().build();
	}

	/**
	 * Obtiene los usuarios asociados a una organización.
	 *
	 * @param id identificador de la organización
	 * @return lista de usuarios de la organización
	 */
	@GetMapping("/{id}/users")
	public List<UserPlanifika> getUsersByOrganization(@PathVariable Long id) {
		return organizationService.getUsersByOrganization(id);
	}
}
