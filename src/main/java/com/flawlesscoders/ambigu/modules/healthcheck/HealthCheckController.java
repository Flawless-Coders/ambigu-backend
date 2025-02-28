package com.flawlesscoders.ambigu.modules.healthcheck;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/health")
@RequiredArgsConstructor
public class HealthCheckController {
    
    private final HealthCheckService healthCheckService;

    @Operation(summary = "Check the status of the server")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Server is up and running"),
        @ApiResponse(responseCode = "503", description = "Server is down because of a database error"),
    })
    @GetMapping
    public ResponseEntity<String> healthCheck() {
        return healthCheckService.healthCheck();
    }
}
