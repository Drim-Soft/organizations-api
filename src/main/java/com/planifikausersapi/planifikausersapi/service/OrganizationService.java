package com.planifikausersapi.planifikausersapi.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.planifikausersapi.planifikausersapi.model.Organization;
import com.planifikausersapi.planifikausersapi.model.UserPlanifika;
import com.planifikausersapi.planifikausersapi.repository.OrganizationRepository;
import com.planifikausersapi.planifikausersapi.repository.UserPlanifikaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Servicio para gestionar operaciones relacionadas con organizaciones.
 */
@Service
public class OrganizationService {

	private final OrganizationRepository organizationRepository;
	private final UserPlanifikaRepository userPlanifikaRepository;

	/**
	 * Constructor para inyección de dependencias.
	 *
	 * @param organizationRepository repositorio de organizaciones
	 * @param userPlanifikaRepository repositorio de usuarios
	 */
	public OrganizationService(OrganizationRepository organizationRepository,
							   UserPlanifikaRepository userPlanifikaRepository) {
		this.organizationRepository = organizationRepository;
		this.userPlanifikaRepository = userPlanifikaRepository;
	}

	/**
	 * Obtiene todas las organizaciones.
	 *
	 * @return lista de todas las organizaciones
	 */
	public List<Organization> findAll() {
		return organizationRepository.findAll();
	}

	/**
	 * Obtiene organizaciones paginadas.
	 *
	 * @param page número de página (inicia en 0)
	 * @param size tamaño de la página
	 * @return página de organizaciones
	 */
	public Page<Organization> findAllPaginated(int page, int size) {
		Pageable pageable = PageRequest.of(page, size);
		return organizationRepository.findAll(pageable);
	}

	/**
	 * Obtiene organizaciones paginadas con filtro de búsqueda.
	 *
	 * @param page número de página (inicia en 0)
	 * @param size tamaño de la página
	 * @param search término de búsqueda
	 * @return página de organizaciones filtradas
	 */
	public Page<Organization> findAllPaginated(int page, int size, String search) {
		Pageable pageable = PageRequest.of(page, size);
		if (search == null || search.trim().isEmpty()) {
			return organizationRepository.findAll(pageable);
		}
		return organizationRepository.findBySearchTerm(search.trim(), pageable);
	}

	/**
	 * Busca una organización por su ID.
	 *
	 * @param id identificador de la organización
	 * @return Optional con la organización si existe
	 */
	public Optional<Organization> findById(Long id) {
		return organizationRepository.findById(id);
	}

	/**
	 * Guarda una organización (crea o actualiza).
	 *
	 * @param organization organización a guardar
	 * @return organización guardada
	 */
	public Organization save(Organization organization) {
		return organizationRepository.save(organization);
	}

	/**
	 * Elimina una organización por su ID.
	 *
	 * @param id identificador de la organización a eliminar
	 */
	public void deleteById(Long id) {
		organizationRepository.deleteById(id);
	}

	/**
	 * Obtiene todos los usuarios asociados a una organización.
	 * Utiliza una consulta directa para evitar problemas de LazyInitializationException y N+1.
	 *
	 * @param id identificador de la organización
	 * @return lista de usuarios de la organización
	 */
	public List<UserPlanifika> getUsersByOrganization(Long id) {
		return userPlanifikaRepository.findByOrganization_Id(id);
	}
}
