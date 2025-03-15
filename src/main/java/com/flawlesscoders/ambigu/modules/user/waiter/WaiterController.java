package com.flawlesscoders.ambigu.modules.user.waiter;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.flawlesscoders.ambigu.modules.user.waiter.DTO.GetWaiterDTO;
import com.flawlesscoders.ambigu.modules.user.waiter.DTO.GetWaiterWAvatarDTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;

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
    public ResponseEntity<List<GetWaiterDTO>> getAllWaiters() {
        return waiterService.getAllWaiters();
    }

    //OBTAIN ALL ACTIVE WAITERS
    @Operation(summary = "Obtains all active waiters")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Active waiters obtained"),
        @ApiResponse(responseCode = "404", description = "No active waiters found"),
    })
    @GetMapping("/active")
    public ResponseEntity<List<GetWaiterDTO>> getAllActiveWaiters() {
        return waiterService.getAllActiveWaiters();
    }

    //OBTAIN WAITER BY EMAIL
    @Operation(summary = "Obtains a waiter by email")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Waiter obtained"),
        @ApiResponse(responseCode = "404", description = "Waiter not found"),
    })
    @GetMapping("/email/{email}")
    public ResponseEntity<GetWaiterDTO> getWaiterByEmail(
        @Parameter(description = "Waiter's email", required = true)
        @PathVariable String email) {
        return waiterService.getWaiterByEmail(email);
    }

    //OBTAIN WAITER WITH AVATAR BY EMAIL
    @Operation(summary = "Obtains a waiter with avatar by email")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Waiter obtained"),
        @ApiResponse(responseCode = "404", description = "Waiter not found"),
    })
    @GetMapping("/email/avatar/{email}")
    public ResponseEntity<GetWaiterWAvatarDTO> getWaiterWAvatarByEmail(
        @Parameter(description = "Waiter's email", required = true)
        @PathVariable String email) {
        return waiterService.getWaiterWAvatarByEmail(email);
    }

    //CREATE A WAITER
    @Operation(summary = "Creates a waiter")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Waiter created"),
        @ApiResponse(responseCode = "400", description = "Invalid waiter"),
    })
    @PostMapping
    public ResponseEntity<Waiter> createWaiter(@RequestBody @Valid Waiter waiter) {
        return waiterService.createWaiter(waiter);
    }


    //UPDATE A WAITER
    @Operation(summary = "Updates a waiter")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Waiter updated"),
        @ApiResponse(responseCode = "400", description = "Invalid waiter"),
        @ApiResponse(responseCode = "404", description = "Waiter not found"),
    })
    @PutMapping()
    public ResponseEntity<Void> updateWaiter(
        @RequestBody 
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Waiter to be updated",
            required = true,
            content = @Content(
                schema = @Schema(implementation = Waiter.class))
        )
        Waiter waiter ) {
        return waiterService.updateWaiter(waiter);
    }

    //UPDATE A WAITER'S AVATAR
    @Operation(summary = "Updates a waiter's avatar")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Avatar updated"),
        @ApiResponse(responseCode = "404", description = "Waiter not found"),
        @ApiResponse(responseCode = "400", description = "Error uploading image")
    })
    //When a client needs to replace an existing Resource entirely, they can use PUT. When they’re doing a partial update, they can use HTTP PATCH.
    @PatchMapping(value = "/avatar/{id}", consumes = {"multipart/form-data"})
    public ResponseEntity<Void> updateWatierAvatar(
        @Parameter(description = "Waiter's id", required = true)
        @PathVariable String id,
        @RequestPart("avatar") MultipartFile avatar
        ) {
        
        return waiterService.updateWaiterAvatar(id, avatar);
    }

    //CHANGE STATUS OF A WAITER
    @Operation(summary = "Changes the status of a waiter")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Status changed"),
        @ApiResponse(responseCode = "404", description = "Waiter not found"),
    })
    @PatchMapping("/status/{id}")
    public ResponseEntity<Void> changeWaiterStatus(
        @Parameter(description = "Waiter's id", required = true)
        @PathVariable String id ) {
        return waiterService.changeWaiterStatus(id);
    }

    @Operation(summary = "Obtains all active waiters who aren't leaders")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Active waiters obtained"),
        @ApiResponse(responseCode = "404", description = "No active waiters found"),
    })
    @GetMapping("/waitersWAvatar")
    public ResponseEntity<List<GetWaiterWAvatarDTO>> getWaitersWAvatar(){
        return waiterService.getWaitersWAvatar();
    }
    
}
