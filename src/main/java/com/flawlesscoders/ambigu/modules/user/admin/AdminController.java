package com.flawlesscoders.ambigu.modules.user.admin;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.flawlesscoders.ambigu.modules.user.admin.DTO.GetAdminDTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    //GET ADMIN DATA
    @Operation(summary = "Obtains admin data")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Admin data obtained"),
        @ApiResponse(responseCode = "404", description = "Admin not found"),
    })
    @GetMapping("/{id}")
    public ResponseEntity<GetAdminDTO> getAdminData(
        @Parameter(description = "Admin id") @PathVariable String id) {
        return adminService.getAdminById(id);
    }

    //UPDATE ADMIN DATA
    @Operation(summary = "Updates admin data")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Admin data updated"),
        @ApiResponse(responseCode = "404", description = "Admin not found"),
    })
    @PutMapping
    public ResponseEntity<GetAdminDTO> updateAdminData(
        @RequestBody Admin admin) {
        adminService.updateAdmin(admin);
        return ResponseEntity.ok().build();
    }



}
