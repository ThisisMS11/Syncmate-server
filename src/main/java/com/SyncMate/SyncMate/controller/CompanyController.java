package com.SyncMate.SyncMate.controller;

import com.SyncMate.SyncMate.dto.CompanyDto;
import com.SyncMate.SyncMate.entity.File;
import com.SyncMate.SyncMate.services.CompanyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
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

    @Operation(summary = "Save company", description = "saving a new company")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully saved the company",
                    content = @Content(mediaType = "text/plain")),
            @ApiResponse(responseCode = "400", description = "Invalid user request!"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error"),
    })
    @PostMapping
    public ResponseEntity<?> saveCompany(@Valid @RequestBody CompanyDto companyDto) {
        companyService.saveCompany(companyDto);
        return ResponseEntity.ok("Company saved successfully");
    }

    @Operation(summary = "Update company", description = "updating a new company")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated the company",
                    content = @Content(mediaType = "text/plain")),
            @ApiResponse(responseCode = "404", description = "Resource not found"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error"),
    })
    @PutMapping
    public ResponseEntity<?> updateCompany(@RequestBody CompanyDto companyDto) {
        companyService.saveCompany(companyDto);
        return ResponseEntity.ok("Company updated successfully");
    }

    @Operation(summary = "Get all companies", description = "Returns a list of all companies")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the company list",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = CompanyDto.class)))),
            @ApiResponse(responseCode = "404", description = "No companies found"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @GetMapping
    public ResponseEntity<List<CompanyDto>> getCompanies() {
        List<CompanyDto> companies = companyService.getCompanies();

        if (companies != null && !companies.isEmpty()) {
            return new ResponseEntity<>(companies, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

}
