package com.SyncMate.SyncMate.services;

import com.SyncMate.SyncMate.dto.CompanyDto;
import com.SyncMate.SyncMate.entity.Company;
import com.SyncMate.SyncMate.exception.CommonExceptions;
import com.SyncMate.SyncMate.repository.CompanyRepository;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
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

    public void saveCompany(CompanyDto companyDto) {
        // Validate the CompanyDto
        log.info("Starting to save company with ID: {}", companyDto.getId());

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        var violations = validator.validate(companyDto);

        if (!violations.isEmpty()) {
            // Handle validation errors
            for (var violation : violations) {
                log.error("Validation error for field {}: {}", violation.getPropertyPath(), violation.getMessage());
            }
            throw new IllegalArgumentException("Validation failed");
        }

        // If ID is null, create; otherwise, update
        if (companyDto.getId() == null) {
            log.info("Creating new company");
            createCompany(companyDto);
        } else {
            log.info("Updating existing company with ID: {}", companyDto.getId());
            updateCompany(companyDto);
        }
    }

    private void createCompany(CompanyDto companyDto) {
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
    }

    private void updateCompany(CompanyDto companyDto) {
        // Logic for updating an existing company
        log.info("Updating company with ID: {}", companyDto.getId());

        Company existingCompany = companyRepository.findById(companyDto.getId())
                .orElseThrow(() -> {
                    log.error("Company with ID {} not found", companyDto.getId());
                    return CommonExceptions.resourceNotFound(String.valueOf(companyDto.getId()));
                });

        // Update fields from DTO
        existingCompany.setName(companyDto.getName());
        existingCompany.setDomain(companyDto.getDomain());
        existingCompany.setLogo(companyDto.getLogo());

        // Save updated company
        companyRepository.save(existingCompany);
        log.info("Successfully updated company with ID: {}", existingCompany.getId());
    }

    public List<CompanyDto> getCompanies(){
        log.info("Finding out all the companies");
        List<CompanyDto> companies = companyRepository.findAll()
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
        return companies;
    }
}
