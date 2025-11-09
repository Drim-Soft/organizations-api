package com.planifikausersapi.planifikausersapi.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.planifikausersapi.planifikausersapi.model.Organization;
import com.planifikausersapi.planifikausersapi.model.UserPlanifika;
import com.planifikausersapi.planifikausersapi.service.OrganizationService;

import java.util.List;

@RestController
@RequestMapping("/organizations")
@CrossOrigin(origins = "*")
public class OrganizationController {

	private final OrganizationService organizationService;

	public OrganizationController(OrganizationService organizationService) {
		this.organizationService = organizationService;
	}

	@GetMapping
	public List<Organization> getAll() {
		return organizationService.findAll();
	}

	@GetMapping("/paginated")
	public Page<Organization> getAllPaginated(
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size,
			@RequestParam(required = false) String search) {
		if (search != null && !search.trim().isEmpty()) {
			return organizationService.findAllPaginated(page, size, search);
		}
		return organizationService.findAllPaginated(page, size);
	}

	@GetMapping("/{id}")
	public ResponseEntity<Organization> getById(@PathVariable Long id) {
		return organizationService.findById(id)
				.map(ResponseEntity::ok)
				.orElse(ResponseEntity.notFound().build());
	}

	@PostMapping
	public Organization create(@RequestBody Organization organization) {
		return organizationService.save(organization);
	}

	@PutMapping("/{id}")
	public ResponseEntity<Organization> update(@PathVariable Long id, @RequestBody Organization organization) {
		return organizationService.findById(id)
				.map(existing -> {
					organization.setId(id);
					return ResponseEntity.ok(organizationService.save(organization));
				})
				.orElse(ResponseEntity.notFound().build());
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		organizationService.deleteById(id);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/{id}/users")
	public List<UserPlanifika> getUsersByOrganization(@PathVariable Long id) {
		return organizationService.getUsersByOrganization(id);
	}
}
