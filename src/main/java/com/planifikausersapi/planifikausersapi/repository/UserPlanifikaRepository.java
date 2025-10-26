package com.planifikausersapi.planifikausersapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.planifikausersapi.planifikausersapi.model.UserPlanifika;

@Repository
public interface UserPlanifikaRepository extends JpaRepository<UserPlanifika, Long> {
}
