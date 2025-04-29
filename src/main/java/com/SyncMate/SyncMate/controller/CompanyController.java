package com.SyncMate.SyncMate.controller;
import com.SyncMate.SyncMate.dto.CompanyDto;
import com.SyncMate.SyncMate.dto.ContactDto;
import com.SyncMate.SyncMate.entity.Company;
import com.SyncMate.SyncMate.repository.CompanyRepository;
import com.SyncMate.SyncMate.services.CompanyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/company")
public class CompanyController {

    @Autowired
    CompanyService companyService;

    @PostMapping
    public ResponseEntity<?> saveCompany(@RequestBody CompanyDto companyDto){
        companyService.saveCompany(companyDto);
        return ResponseEntity.ok("Company saved successfully");
    }

    @PutMapping
    public ResponseEntity<?> updateCompany(@RequestBody CompanyDto companyDto){
        companyService.saveCompany(companyDto);
        return ResponseEntity.ok("Company updated successfully");
    }

    @GetMapping
    public ResponseEntity<?> getCompanies(){
        List<CompanyDto> companies = companyService.getCompanies();

        if(companies!=null && !companies.isEmpty()){
            return new ResponseEntity<>(companies,HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

}
