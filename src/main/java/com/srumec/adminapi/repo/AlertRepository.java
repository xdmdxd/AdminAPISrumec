package com.srumec.adminapi.repo;

import com.srumec.adminapi.domain.Alert;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AlertRepository extends JpaRepository<Alert, UUID> {
}
