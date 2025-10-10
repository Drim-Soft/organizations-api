package com.planifikausersapi.planifikausersapi.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.planifikausersapi.planifikausersapi.model.Organization;

@Repository
public interface OrganizationRepository extends JpaRepository<Organization, Long> {
}
