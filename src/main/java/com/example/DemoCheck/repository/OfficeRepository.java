package com.example.DemoCheck.repository;

import com.example.DemoCheck.entity.Office;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "offices")
public interface OfficeRepository extends JpaRepository<Office, String> {
}