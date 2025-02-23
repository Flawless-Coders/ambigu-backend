package com.flawlesscoders.ambigu.modules.user.admin;

import java.io.IOException;
import java.util.Base64;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.multipart.MultipartFile;

import com.flawlesscoders.ambigu.modules.user.admin.DTO.GetAdminDTO;

import io.micrometer.core.ipc.http.HttpSender.Response;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminService {
    private AdminRepository adminRepository;

    private GetAdminDTO transformAdminToDTO (Admin admin) {
        return GetAdminDTO.builder()
            .id(admin.getId())
            .name(admin.getName())
            .lastname_p(admin.getLastname_p())
            .lastname_m(admin.getLastname_m())
            .email(admin.getEmail())
            .build();
    }

    public ResponseEntity<GetAdminDTO> getAdminById(String id) {
        Admin admin = adminRepository.findAdminById(id).orElseThrow(() -> new RuntimeException("Admin not found"));
        return ResponseEntity.ok(transformAdminToDTO(admin));
    }

    public ResponseEntity<Void> updateAdmin(Admin admin){
        Admin existingAdmin = adminRepository.findAdminById(admin.getId()).orElseThrow(() -> new RuntimeException("Admin not found"));
        existingAdmin.setName(admin.getName());
        existingAdmin.setLastname_p(admin.getLastname_p());
        existingAdmin.setLastname_m(admin.getLastname_m());
        existingAdmin.setEmail(admin.getEmail());

        adminRepository.save(existingAdmin);
        return ResponseEntity.ok().build();
    }

    public ResponseEntity<Void> updateAdminPassword(Admin admin){
        Admin existingAdmin = adminRepository.findAdminById(admin.getId()).orElseThrow(() -> new RuntimeException("Admin not found"));
        existingAdmin.setPassword(admin.getPassword());
        adminRepository.save(existingAdmin);
        return ResponseEntity.ok().build();
    }

    public ResponseEntity<Void> updateAdminAvatar(String id, MultipartFile avatar) {
        try{
            Admin existingAdmin = adminRepository.findAdminById(id).orElseThrow(() -> new RuntimeException("Admin not found"));
            String base64imag = Base64.getEncoder().encodeToString(avatar.getBytes());
            existingAdmin.setAvatarBase64(base64imag);
            adminRepository.save(existingAdmin);
            return ResponseEntity.ok().build();
        } catch (IOException e) {
            throw new RuntimeException("Error uploading image");
    }
    }

}
