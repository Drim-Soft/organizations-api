package com.planifikausersapi.planifikausersapi.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.planifikausersapi.planifikausersapi.model.Organization;
import com.planifikausersapi.planifikausersapi.model.UserPlanifika;
import com.planifikausersapi.planifikausersapi.repository.OrganizationRepository;

import java.util.List;
import java.util.Optional;

@Service
public class OrganizationService {

	private final OrganizationRepository organizationRepository;

	public OrganizationService(OrganizationRepository organizationRepository) {
		this.organizationRepository = organizationRepository;
	}

	public List<Organization> findAll() {
		return organizationRepository.findAll();
	}

	public Page<Organization> findAllPaginated(int page, int size) {
		Pageable pageable = PageRequest.of(page, size);
		return organizationRepository.findAll(pageable);
	}

	public Page<Organization> findAllPaginated(int page, int size, String search) {
		Pageable pageable = PageRequest.of(page, size);
		if (search == null || search.trim().isEmpty()) {
			return organizationRepository.findAll(pageable);
		}
		return organizationRepository.findBySearchTerm(search.trim(), pageable);
	}

	public Optional<Organization> findById(Long id) {
		return organizationRepository.findById(id);
	}

	public Organization save(Organization organization) {
		return organizationRepository.save(organization);
	}

	public void deleteById(Long id) {
		organizationRepository.deleteById(id);
	}

	public List<UserPlanifika> getUsersByOrganization(Long id) {
		Optional<Organization> org = organizationRepository.findById(id);
		return org.map(Organization::getUsers).orElse(List.of());
	}
}
