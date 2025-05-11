package com.SyncMate.SyncMate.controller;

import com.SyncMate.SyncMate.dto.ContactDto;
import com.SyncMate.SyncMate.services.ContactService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/contact")
public class ContactController {

    @Autowired
    private ContactService contactService;

    @Operation(summary = "Save new contact", description = "saving contact")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully saved the contact",
                    content = @Content(mediaType = "text/plain")),
            @ApiResponse(responseCode = "400", description = "Invalid user request!"),
            @ApiResponse(responseCode = "404", description = "Company not found"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error"),
    })
    @PostMapping
    public ResponseEntity<?> saveContact(@Valid @RequestBody ContactDto contactDto) {
        contactService.saveContact(contactDto);
        return ResponseEntity.ok("Contact saved successfully");
    }

    @Operation(summary = "Update a existing contact", description = "updating a contact")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated the contact",
                    content = @Content(mediaType = "text/plain")),
            @ApiResponse(responseCode = "400", description = "Invalid user request!"),
            @ApiResponse(responseCode = "403", description = "Forbidden access to this resource"),
            @ApiResponse(responseCode = "404", description = "Resource not found"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error"),
    })
    @PutMapping
    public ResponseEntity<?> updateContact(@RequestBody ContactDto contactDto) {
        contactService.saveContact(contactDto);
        return ResponseEntity.ok("Contact Updated successfully");
    }

    @Operation(summary = "Delete contact", description = "Deleting a contact")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully deleted the contact",
                    content = @Content(mediaType = "text/plain")),
            @ApiResponse(responseCode = "403", description = "Forbidden user access"),
            @ApiResponse(responseCode = "404", description = "Contact not found"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error"),
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteContact(@PathVariable Long id) {
        contactService.deleteContact(id);
        return ResponseEntity.ok("Contact Deleted successfully");
    }
}
