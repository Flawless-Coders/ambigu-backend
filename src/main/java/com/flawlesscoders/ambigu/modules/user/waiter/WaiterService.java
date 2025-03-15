package com.flawlesscoders.ambigu.modules.user.waiter;

import java.io.IOException;
import java.util.Base64;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.flawlesscoders.ambigu.modules.user.base.Role;
import com.flawlesscoders.ambigu.modules.user.waiter.DTO.GetWaiterDTO;
import com.flawlesscoders.ambigu.modules.user.waiter.DTO.GetWaiterWAvatarDTO;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WaiterService {

    private final WaiterRepository waiterRepository;
    private final PasswordEncoder passwordEncoder;

    private GetWaiterDTO toGetWaiterDTO(Waiter waiter) {
        return GetWaiterDTO.builder()
            .id(waiter.getId())
            .name(waiter.getName())
            .lastname_p(waiter.getLastname_p())
            .lastname_m(waiter.getLastname_m())
            .email(waiter.getEmail())
            .phone(waiter.getPhone())
            .isLeader(waiter.isLeader())
            .AvgRating(waiter.getAvgRating())
            .build();
    }

    private GetWaiterWAvatarDTO toGetWaiterWAvatarDTO(Waiter waiter) {
        return GetWaiterWAvatarDTO.builder()
            .id(waiter.getId())
            .name(waiter.getName())
            .lastname_p(waiter.getLastname_p())
            .lastname_m(waiter.getLastname_m())
            .email(waiter.getEmail())
            .avatarBase64(waiter.getAvatarBase64())
            .phone(waiter.getPhone())
            .isLeader(waiter.isLeader())
            .AvgRating(waiter.getAvgRating())
            .build();
    }

    public ResponseEntity<List<GetWaiterDTO>> getAllWaiters() {
        List<Waiter> waiters = waiterRepository.findAllWaiters();
        return ResponseEntity.ok(waiters.stream().map(this::toGetWaiterDTO).toList());
    }

    public ResponseEntity<List<GetWaiterDTO>> getAllActiveWaiters() {
        List<Waiter> waiters = waiterRepository.findAllByStatusTrue();
        return ResponseEntity.ok(waiters.stream().map(this::toGetWaiterDTO).toList());
    }

    /**
     * 
     * @throws ResponseStatusException 404 si no se encuentra el mesero.
     */
    public ResponseEntity<Waiter> getWaiterById(String id) {
        Waiter waiter = waiterRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Waiter not found"));
        return ResponseEntity.ok(waiter);
    }

    public ResponseEntity<GetWaiterDTO> getWaiterByEmail(String email) {
        Waiter waiter = waiterRepository.findByEmail(email)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Waiter not found"));
        return ResponseEntity.ok(toGetWaiterDTO(waiter));
    }

    public ResponseEntity<GetWaiterWAvatarDTO> getWaiterWAvatarByEmail(String email) {
        Waiter waiter = waiterRepository.findByEmail(email)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Waiter not found"));
        return ResponseEntity.ok(toGetWaiterWAvatarDTO(waiter));
    }

    public ResponseEntity<Waiter> createWaiter(@Valid Waiter waiter) {
        if(waiterRepository.findByEmail(waiter.getEmail()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already in use");
        }
        if(waiter.getPassword() == null) {
            String password = waiter.getName().substring(0, 1).toUpperCase() + waiter.getLastname_p().substring(0, 1).toUpperCase() + waiter.getLastname_m().substring(0, 1).toUpperCase() + waiter.getEmail().substring(0, 1).toUpperCase();
            waiter.setPassword(password);
        }
        waiter.setRole(Role.WAITER);
        waiter.setStatus(true);
        waiter.setLeader(false);
        waiter.setPassword(passwordEncoder.encode(waiter.getPassword()));
        Waiter savedWaiter = waiterRepository.save(waiter);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedWaiter);
    }

    public ResponseEntity<Void> updateWaiter(@Valid Waiter waiter) {
        Waiter existingWaiter = waiterRepository.findById(waiter.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Mesero no encontrado"));

        existingWaiter.setName(waiter.getName());
        existingWaiter.setLastname_p(waiter.getEmail());
        existingWaiter.setLastname_m(waiter.getPassword());
        existingWaiter.setEmail(waiter.getEmail());
        
        waiterRepository.save(existingWaiter);
        return ResponseEntity.ok().build();
    }

    public ResponseEntity<Void> updateWaiterAvatar (String id, MultipartFile avatar) {
        try {
            Waiter existingWaiter = waiterRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Mesero no encontrado"));
            String contentType = avatar.getContentType();
            if(contentType == null || !contentType.startsWith("image/")) {
                return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).build();
            }
            String base64Image = "data:" + contentType + ";base64," + Base64.getEncoder().encodeToString(avatar.getBytes());
            existingWaiter.setAvatarBase64(base64Image);
            waiterRepository.save(existingWaiter);
            return ResponseEntity.ok().build();
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error al guardar la imagen");
        }
    }

    public ResponseEntity<Void> changeWaiterStatus(String id) {
        Waiter existingWaiter = waiterRepository.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Mesero no encontrado"));

        existingWaiter.setStatus(!existingWaiter.isStatus());
        return ResponseEntity.ok().build();
    }
    
    public ResponseEntity<List<GetWaiterWAvatarDTO>> getWaitersWAvatar() {
        List<Waiter> waiters = waiterRepository.findAllByStatusTrueAndLeaderFalse();
        return ResponseEntity.ok(waiters.stream().map(this::toGetWaiterWAvatarDTO).toList());
    }
}
