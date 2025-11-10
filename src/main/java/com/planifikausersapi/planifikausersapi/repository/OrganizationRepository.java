package com.planifikausersapi.planifikausersapi.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.planifikausersapi.planifikausersapi.model.Organization;

@Repository
public interface OrganizationRepository extends JpaRepository<Organization, Long> {
	
	@Query("SELECT o FROM Organization o WHERE " +
		   "LOWER(o.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
		   "LOWER(o.nit) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
		   "LOWER(o.domain) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
		   "LOWER(o.address) LIKE LOWER(CONCAT('%', :search, '%'))")
	Page<Organization> findBySearchTerm(@Param("search") String search, Pageable pageable);
}
