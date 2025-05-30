package com.SyncMate.SyncMate.controller;

import com.SyncMate.SyncMate.dto.UserinfoDto;
import com.SyncMate.SyncMate.dto.common.MakeResponseDto;
import com.SyncMate.SyncMate.dto.responses.user.UserInfoResponse;
import com.SyncMate.SyncMate.entity.User;
import com.SyncMate.SyncMate.enums.Role;
import com.SyncMate.SyncMate.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserService userService;

    @Operation(summary = "Get User information", description = "get user information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully Fetched user information",
                    content = @Content(schema = @Schema(implementation = UserInfoResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid user request!"),
    })
    @GetMapping
    public ResponseEntity<MakeResponseDto<?>> getUserInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.getUserByEmail(authentication.getName());

        List<Role> roleList = new ArrayList<>(user.getRoles());
        UserinfoDto userinfoDto = new UserinfoDto(user.getId(), user.getUsername(), user.getEmail(), roleList);
        // Return success response without tokens in body
        MakeResponseDto<?> finalResponse = new MakeResponseDto<>(true, "User logged in successfully", userinfoDto);
        return ResponseEntity.ok(finalResponse);
    }
}
