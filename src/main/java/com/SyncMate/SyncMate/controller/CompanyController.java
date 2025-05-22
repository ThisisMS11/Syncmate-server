package com.SyncMate.SyncMate.controller;

import com.SyncMate.SyncMate.dto.CompanyDto;
import com.SyncMate.SyncMate.dto.common.MakeResponseDto;
import com.SyncMate.SyncMate.dto.responses.company.CompanyListResponse;
import com.SyncMate.SyncMate.dto.responses.company.CompanyResponse;
import com.SyncMate.SyncMate.entity.Company;
import com.SyncMate.SyncMate.exception.ApiError;
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
                    content = @Content(schema = @Schema(implementation = CompanyResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid user request!", content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(schema = @Schema(implementation = ApiError.class))),
    })
    @PostMapping
    public ResponseEntity<MakeResponseDto<Company>> saveCompany(@Valid @RequestBody CompanyDto companyDto) {
        Company company = companyService.saveCompany(companyDto);
        MakeResponseDto<Company> finalResponse = new MakeResponseDto<>(true, "Company saved successfully", company);
        return ResponseEntity.ok(finalResponse);
    }

    @Operation(summary = "Update company", description = "updating a new company")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated the company",
                    content = @Content(schema = @Schema(implementation = CompanyResponse.class))),
            @ApiResponse(responseCode = "404", description = "Resource not found", content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(schema = @Schema(implementation = ApiError.class))),
    })
    @PutMapping
    public ResponseEntity<MakeResponseDto<Company>> updateCompany(@RequestBody CompanyDto companyDto) {
        Company company = companyService.saveCompany(companyDto);
        MakeResponseDto<Company> finalResponse = new MakeResponseDto<>(true, "Company updated successfully", company);
        return ResponseEntity.ok(finalResponse);
    }

    @Operation(summary = "Get all companies", description = "Returns a list of all companies")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the company list",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = CompanyListResponse.class)))),
            @ApiResponse(responseCode = "404", description = "No companies found", content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @GetMapping
    public ResponseEntity<MakeResponseDto<List<CompanyDto>>> getCompanies() {
        List<CompanyDto> companies = companyService.getCompanies();
        MakeResponseDto<List<CompanyDto>> finalResponse = new MakeResponseDto<>(true, "Companies fetched successfully", companies);
        return ResponseEntity.ok(finalResponse);
    }

}
