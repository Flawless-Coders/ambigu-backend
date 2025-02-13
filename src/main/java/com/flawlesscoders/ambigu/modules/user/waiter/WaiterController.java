package com.flawlesscoders.ambigu.user.waiter;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("api/waiters")
@RequiredArgsConstructor
public class WaiterController {

    private final WaiterService waiterService;

    //OBTAIN ALL WAITERS
    @Operation(summary = "Obtains all waiters")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Waiters obtained"),
    })
    @GetMapping
    public ResponseEntity<List<Waiter>> getAllWaiters() {
        List<Waiter> waiters = waiterService.getAllWaiters();
        return ResponseEntity.ok(waiters);
    }

    //OBTAIN ALL ACTIVE WAITERS
    @Operation(summary = "Obtains all active waiters")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Active waiters obtained"),
        @ApiResponse(responseCode = "404", description = "No active waiters found"),
    })
    @GetMapping("/active")
    public ResponseEntity<List<Waiter>> getAllActiveWaiters() {
        List<Waiter> waiters = waiterService.getAllActiveWaiters();
        return ResponseEntity.ok(waiters);
    }

    //CREATE A WAITER
    @Operation(summary = "Creates a waiter")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Waiter created"),
        @ApiResponse(responseCode = "400", description = "Invalid waiter"),
    })
    @PostMapping
    public ResponseEntity<Waiter> createWaiter(
        @RequestBody 
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Waiter to be created",
            required = true,
            content = @Content(
                schema = @Schema(implementation = Waiter.class))
        )
        Waiter waiter ) {
        Waiter createdWaiter = waiterService.createWaiter(waiter);
        return ResponseEntity.status(201).body(createdWaiter);
    }

    //UPDATE A WAITER
    @Operation(summary = "Updates a waiter")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Waiter updated"),
        @ApiResponse(responseCode = "400", description = "Invalid waiter"),
        @ApiResponse(responseCode = "404", description = "Waiter not found"),
    })
    @PutMapping("/update")
    public ResponseEntity<Waiter> updateWaiter(
        @RequestBody 
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Waiter to be updated",
            required = true,
            content = @Content(
                schema = @Schema(implementation = Waiter.class))
        )
        Waiter waiter ) {
        Waiter updatedWaiter = waiterService.updateWaiter(waiter);
        return ResponseEntity.ok(updatedWaiter);
    }

    //CHANGE STATUS OF A WAITER
    @Operation(summary = "Changes the status of a waiter")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Status changed"),
        @ApiResponse(responseCode = "404", description = "Waiter not found"),
    })
    @PutMapping("/status/{id}")
    public ResponseEntity<Waiter> changeWaiterStatus(
        @Parameter(description = "Waiter's id", required = true)
        @PathVariable String id ) {
        Waiter updatedWaiter = waiterService.changeWaiterStatus(id);
        return ResponseEntity.ok(updatedWaiter);
    }

    
}
