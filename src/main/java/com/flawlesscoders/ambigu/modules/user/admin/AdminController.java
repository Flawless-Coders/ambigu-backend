package com.flawlesscoders.ambigu.modules.user.admin;

import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.flawlesscoders.ambigu.modules.user.admin.DTO.GetAdminDTO;
import com.flawlesscoders.ambigu.modules.user.admin.DTO.UpdatePasswordDTO;

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
    @GetMapping("/{email}")
    public ResponseEntity<GetAdminDTO> getAdminData(
        @Parameter(description = "Admin email") @PathVariable String email) {
        return adminService.getAdminByEmail(email);
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

    //UPDATE ADMIN AVATAR
    @Operation(summary = "Updates admin avatar")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Admin avatar updated"),
        @ApiResponse(responseCode = "404", description = "Admin not found"),
    })
    @PatchMapping(value = "/avatar/{id}", consumes = {"multipart/form-data"})
    public ResponseEntity<Void> updateAdminAvatar(
        @Parameter(description = "Admin id", required = true) 
        @PathVariable String id,
        @Parameter(description = "Admin avatar file", required = true)
        @RequestPart("avatar") MultipartFile avatar
        ) {
        return adminService.updateAdminAvatar(id, avatar);
    }

    //UPDATE ADMIN PASSWORD
    @Operation(summary = "Updates admin password")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Admin password updated"),
        @ApiResponse(responseCode = "404", description = "Admin not found"),
    })
    @PatchMapping("/password")
    public ResponseEntity<Void> updateAdminPassword(
        @RequestBody UpdatePasswordDTO updatePasswordDTO) {
        return adminService.updateAdminPassword(updatePasswordDTO);
    }



}
