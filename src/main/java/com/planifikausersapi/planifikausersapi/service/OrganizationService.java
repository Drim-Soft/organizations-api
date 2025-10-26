package com.planifikausersapi.planifikausersapi.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.planifikausersapi.planifikausersapi.model.Organization;
import com.planifikausersapi.planifikausersapi.model.UserPlanifika;
import com.planifikausersapi.planifikausersapi.repository.OrganizationRepository;
import com.planifikausersapi.planifikausersapi.repository.UserPlanifikaRepository;

import java.util.List;
import java.util.Optional;

@Service
public class OrganizationService {

	private final OrganizationRepository organizationRepository;
	private final UserPlanifikaRepository userPlanifikaRepository;

	public OrganizationService(OrganizationRepository organizationRepository, UserPlanifikaRepository userPlanifikaRepository) {
		this.organizationRepository = organizationRepository;
		this.userPlanifikaRepository = userPlanifikaRepository;
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

	@Transactional
	public Optional<UserPlanifika> linkUserToOrganization(Long organizationId, Long userId) {
		Optional<Organization> organization = organizationRepository.findById(organizationId);
		Optional<UserPlanifika> user = userPlanifikaRepository.findById(userId);
		
		if (organization.isPresent() && user.isPresent()) {
			UserPlanifika userToUpdate = user.get();
			userToUpdate.setOrganization(organization.get());
			UserPlanifika savedUser = userPlanifikaRepository.save(userToUpdate);
			return Optional.of(savedUser);
		}
		
		return Optional.empty();
	}
}
