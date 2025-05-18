package com.SyncMate.SyncMate.controller;
import com.SyncMate.SyncMate.dto.APIKey.APIKeyRequestDto;
import com.SyncMate.SyncMate.dto.APIKey.APIKeyResponseDto;
import com.SyncMate.SyncMate.dto.common.MakeResponseDto;
import com.SyncMate.SyncMate.services.APIKeyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/apiKey")
public class APIKeyController {

    @Autowired
    private APIKeyService apiKeyService;


    @Operation(summary = "Create API key", description = "create new api key")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully generated the apikey",
                    content = @Content(schema = @Schema(implementation = APIKeyResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid user request!"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error"),
    })
    @PostMapping
    public ResponseEntity<MakeResponseDto<APIKeyResponseDto>> createAPIKey(@Valid @RequestBody APIKeyRequestDto apiKeyRequestDto) {
        APIKeyResponseDto apiKeyResponse = apiKeyService.createAPIKey(apiKeyRequestDto);
        MakeResponseDto<APIKeyResponseDto> finalResponse = new MakeResponseDto<>(true, "API Key successfully generated", apiKeyResponse);
        return ResponseEntity.ok(finalResponse);
    }

    @Operation(summary = "Delete api key", description = "Deleting a api key")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully deleted the contact",
                    content = @Content(schema = @Schema(implementation = MakeResponseDto.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden user access"),
            @ApiResponse(responseCode = "404", description = "Contact not found"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error"),
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<MakeResponseDto<?>> deleteContact(@PathVariable Long id) {
        apiKeyService.deleteAPIKey(id);
        MakeResponseDto<?> finalResponse = new MakeResponseDto<>(true, "API deleted successfully", null);
        return ResponseEntity.ok(finalResponse);
    }
}
