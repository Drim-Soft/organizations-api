package com.planifikausersapi.planifikausersapi.service;

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
