package com.example.DemoCheck.repository;

import com.example.DemoCheck.entity.Employee;
import com.example.DemoCheck.entity.Office;
import com.example.DemoCheck.projection.OfficeProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import java.util.List;

@RepositoryRestResource(path = "offices",collectionResourceRel = "offices")
public interface OfficeRepository extends JpaRepository<Office, String> {

    @RestResource(path = "by-cities")
    List<Office> findByCityIn(@Param("cities") List<String> cities);

    @RestResource(path = "by-city",rel = "by-city-paged")
    Page<Office> findByCity(@Param("city") String city, Pageable pageable);

    @RestResource(path = "by-state",rel = "by-state")
    Page<Office> findByState(@Param("state") String city, Pageable pageable);

    @RestResource(path = "by-country",rel = "by-country")
    Page<Office> findByCountry(@Param("country") String country, Pageable pageable);

    @RestResource(path = "by-territory",rel = "by-territory")
    Page<Office> findByTerritory(@Param("territory") String territory, Pageable pageable);

}