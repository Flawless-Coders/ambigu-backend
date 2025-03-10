package com.flawlesscoders.ambigu.modules.user.admin;

import java.io.IOException;
import java.util.Base64;

import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.UnsupportedMediaTypeStatusException;

import com.flawlesscoders.ambigu.modules.user.admin.DTO.GetAdminDTO;
import com.flawlesscoders.ambigu.modules.user.admin.DTO.UpdatePasswordDTO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final AdminRepository adminRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private GetAdminDTO transformAdminToDTO (Admin admin) {
        return GetAdminDTO.builder()
            .id(admin.getId())
            .name(admin.getName())
            .lastname_p(admin.getLastname_p())
            .lastname_m(admin.getLastname_m())
            .email(admin.getEmail())
            .phone(admin.getPhone())
            .avatarBase64(admin.getAvatarBase64())
            .build();
    }

    public ResponseEntity<GetAdminDTO> getAdminByEmail(String email) {
        Admin admin = adminRepository.findAdminByEmail(email).orElseThrow(() -> new RuntimeException("Admin not found"));
        return ResponseEntity.ok(transformAdminToDTO(admin));
    }

    public ResponseEntity<Void> updateAdmin(Admin admin){
        Admin existingAdmin = adminRepository.findAdminById(admin.getId()).orElseThrow(() -> new RuntimeException("Admin not found"));
        existingAdmin.setName(admin.getName());
        existingAdmin.setLastname_p(admin.getLastname_p());
        existingAdmin.setLastname_m(admin.getLastname_m());
        existingAdmin.setEmail(admin.getEmail());
        existingAdmin.setPhone(admin.getPhone());

        adminRepository.save(existingAdmin);
        return ResponseEntity.ok().build();
    }

    public ResponseEntity<Void> updateAdminPassword(UpdatePasswordDTO updatePasswordDTO){
        Admin existingAdmin = adminRepository.findAdminById(updatePasswordDTO.getId()).orElseThrow(() -> new RuntimeException("Admin not found"));

        if(!passwordEncoder.matches(updatePasswordDTO.getCurrentPassword(), existingAdmin.getPassword())){
            System.out.println("La contraseña: "+ existingAdmin.getPassword() + " no es igual a: " + updatePasswordDTO.getCurrentPassword());
            throw new RuntimeException("Incorrect password");
        }

        String encodedNewPassword = passwordEncoder.encode(updatePasswordDTO.getNewPassword());
        updatePasswordDTO.setNewPassword(encodedNewPassword);

        existingAdmin.setPassword(updatePasswordDTO.getNewPassword());
        adminRepository.save(existingAdmin);
        return ResponseEntity.ok().build();
    }

    public ResponseEntity<Void> updateAdminAvatar(String id, MultipartFile avatar) {
        try{
            Admin existingAdmin = adminRepository.findAdminById(id).orElseThrow(() -> new RuntimeException("Admin not found"));
            String contentType = avatar.getContentType(); 
            if(contentType == null || !contentType.startsWith("image/")){
                return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).build();
            }
            String base64Image = "data:" + contentType + ";base64," + Base64.getEncoder().encodeToString(avatar.getBytes());
            existingAdmin.setAvatarBase64(base64Image);
            adminRepository.save(existingAdmin);
            return ResponseEntity.ok().build();
        } catch (IOException e) {
            throw new RuntimeException("Error uploading image");
    }
    }

}
