package com.SyncMate.SyncMate.controller;

import com.SyncMate.SyncMate.dto.ContactRequestDto;
import com.SyncMate.SyncMate.dto.UserContactDto;
import com.SyncMate.SyncMate.dto.common.MakeResponseDto;
import com.SyncMate.SyncMate.dto.responses.contact.ContactResponse;
import com.SyncMate.SyncMate.dto.responses.contact.UserContactsResponse;
import com.SyncMate.SyncMate.services.ContactService;
import com.SyncMate.SyncMate.services.UserService;
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
@RequestMapping("/contact")
public class ContactController {

    @Autowired
    private ContactService contactService;

    @Autowired
    private UserService userService;

    @Operation(summary = "Save new contact", description = "saving contact")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully saved the contact",
                    content = @Content(schema = @Schema(implementation = ContactResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid user request!"),
            @ApiResponse(responseCode = "404", description = "Company not found"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error"),
    })
    @PostMapping
    public ResponseEntity<MakeResponseDto<UserContactDto>> saveContact(@Valid @RequestBody ContactRequestDto contactDto) {
        UserContactDto contact = contactService.createContact(contactDto);
        MakeResponseDto<UserContactDto> finalResponse = new MakeResponseDto<>(true, "Contact saved successfully", contact);
        return ResponseEntity.ok(finalResponse);
    }

    @Operation(summary = "Update a existing contact", description = "updating a contact")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated the contact",
                    content = @Content(schema = @Schema(implementation = ContactResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid user request!"),
            @ApiResponse(responseCode = "403", description = "Forbidden access to this resource"),
            @ApiResponse(responseCode = "404", description = "Resource not found"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error"),
    })
    @PutMapping("/{id}")
    public ResponseEntity<MakeResponseDto<UserContactDto>> updateContact(@PathVariable Long id, @RequestBody ContactRequestDto contactDto) {
        UserContactDto contact = contactService.updateContact(id, contactDto);
        MakeResponseDto<UserContactDto> finalResponse = new MakeResponseDto<>(true, "Contact Updated successfully", contact);
        return ResponseEntity.ok(finalResponse);
    }

    @Operation(summary = "Delete contact", description = "Deleting a contact")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully deleted the contact",
                    content = @Content(schema = @Schema(implementation = MakeResponseDto.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden user access"),
            @ApiResponse(responseCode = "404", description = "Contact not found"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error"),
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<MakeResponseDto<?>> deleteContact(@PathVariable Long id) {
        contactService.deleteContact(id);
        MakeResponseDto<?> finalResponse = new MakeResponseDto<>(true, "Contact Updated successfully", null);
        return ResponseEntity.ok(finalResponse);
    }


    @Operation(summary = "Get all users contacts", description = "Returns a list of all users contacts in the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved users contacts",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = UserContactsResponse.class)))
            ),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema()))
    })
    @GetMapping("/user-contacts")
    public ResponseEntity<MakeResponseDto<List<UserContactDto>>> getUserContacts() {
        List<UserContactDto> contacts = userService.getUserContacts();
        MakeResponseDto<List<UserContactDto>> finalResponse = new MakeResponseDto<>(true, "User Contacts fetched successfully", contacts);
        return ResponseEntity.ok(finalResponse);
    }
}
