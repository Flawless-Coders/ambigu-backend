package com.flawlesscoders.ambigu.modules.user.admin;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient.ResponseSpec;

import com.flawlesscoders.ambigu.modules.user.admin.DTO.GetAdminDTO;

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
        Admin admin = adminRepository.findById(id).orElseThrow(() -> new RuntimeException("Admin not found"));
        return ResponseEntity.ok(transformAdminToDTO(admin));
    }

    public ResponseEntity<Void> updateAdmin(Admin admin){
        Admin existingAdmin = adminRepository.findById(admin.getId()).orElseThrow(() -> new RuntimeException("Admin not found"));
        existingAdmin.setName(admin.getName());
        existingAdmin.setLastname_p(admin.getLastname_p());
        existingAdmin.setLastname_m(admin.getLastname_m());
        existingAdmin.setEmail(admin.getEmail());

        adminRepository.save(existingAdmin);
        return ResponseEntity.ok().build();
    }

    public ResponseEntity<Void> updateAdminPassword(Admin admin){
        Admin existingAdmin = adminRepository.findById(admin.getId()).orElseThrow(() -> new RuntimeException("Admin not found"));
        existingAdmin.setPassword(admin.getPassword());
        adminRepository.save(existingAdmin);
        return ResponseEntity.ok().build();
    }

}
