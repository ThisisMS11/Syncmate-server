package com.SyncMate.SyncMate.services;

import com.SyncMate.SyncMate.dto.CompanyDto;
import com.SyncMate.SyncMate.entity.Company;
import com.SyncMate.SyncMate.exception.CommonExceptions;
import com.SyncMate.SyncMate.repository.CompanyRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
@Slf4j
public class CompanyService {

    @Autowired
    private CompanyRepository companyRepository;

    public Company createCompany(CompanyDto companyDto) {
        // Logic for creating a new company
        log.info("Creating a new company with name: {}", companyDto.getName());

        Company company = new Company();
        // Set fields from DTO
        company.setName(companyDto.getName());
        company.setDomain(companyDto.getDomain());
        company.setLogo(companyDto.getLogo());

        // Save to repository
        companyRepository.save(company);
        log.info("Successfully created company with ID: {}", company.getId());
        return company;
    }

    public Company updateCompany(Long id, CompanyDto companyDto) {
        // Logic for updating an existing company
        log.info("Updating company with ID: {}", id);

        Company existingCompany = companyRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Company with ID {} not found", id);
                    return CommonExceptions.resourceNotFound(String.valueOf(id));
                });

        // Update fields from DTO
        existingCompany.setName(companyDto.getName());
        existingCompany.setDomain(companyDto.getDomain());
        existingCompany.setLogo(companyDto.getLogo());

        // Save updated company
        companyRepository.save(existingCompany);
        log.info("Successfully updated company with ID: {}", existingCompany.getId());
        return existingCompany;
    }

    public List<CompanyDto> getCompanies() {
        log.info("Finding out all the companies");
        return companyRepository.findAll()
                .stream()
                .map(company -> new CompanyDto(
                        company.getId(),
                        company.getName(),
                        company.getDomain(),
                        company.getLogo(),
                        company.getCreatedAt(),
                        company.getUpdatedAt()
                ))
                .collect(Collectors.toList());
    }
}
