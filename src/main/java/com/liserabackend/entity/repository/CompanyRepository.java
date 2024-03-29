package com.liserabackend.entity.repository;

import com.liserabackend.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<Company,String> {
    Optional<Company> findByName(String name);
    Optional<Company> findByOrgNumber(String userId);
}
