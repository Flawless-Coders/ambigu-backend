package com.flawlesscoders.ambigu.modules.auth;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @Operation(summary ="Login with platform restriction")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User logged in"),
        @ApiResponse(responseCode = "400", description = "Invalid credentials"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
    })
    @PostMapping("/login")
    public ResponseEntity<String> login(@Validated @RequestBody AuthRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
