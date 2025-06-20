package com.SyncMate.SyncMate.controller;

import com.SyncMate.SyncMate.dto.UserinfoDto;
import com.SyncMate.SyncMate.dto.common.MakeResponseDto;
import com.SyncMate.SyncMate.dto.responses.user.UserInfoResponse;
import com.SyncMate.SyncMate.entity.User;
import com.SyncMate.SyncMate.enums.Role;
import com.SyncMate.SyncMate.services.UserService;
import com.SyncMate.SyncMate.services.UtilService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
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

    @Autowired
    UtilService utilService;


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

    @Operation(summary = "Logout User", description = "get user logout")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully logs out the user"),
            @ApiResponse(responseCode = "400", description = "Invalid user request!"),
    })
    @GetMapping("/logout")
    public ResponseEntity<MakeResponseDto<?>> logoutUser(HttpServletResponse response) {
        Cookie accessTokenCookie = deleteCookie("access_token");
        Cookie refreshTokenCookie = deleteCookie("refresh_token");

        response.addCookie(accessTokenCookie);
        response.addCookie(refreshTokenCookie);
        MakeResponseDto<?> finalResponse = new MakeResponseDto<>(true, "User logged out successfully", null);
        return ResponseEntity.ok(finalResponse);
    }

    private Cookie deleteCookie(String name) {
        Cookie cookie = new Cookie(name, null);
        cookie.setMaxAge(0);
        cookie.setHttpOnly(true);
        boolean isProd = utilService.isProd();
        cookie.setSecure(isProd);
        cookie.setPath("/");
        cookie.setAttribute("SameSite", isProd ? "None" : "Lax");
        cookie.setDomain(isProd ? "mohitsaini.in" : "localhost");// match original
        return cookie;
    }
}
