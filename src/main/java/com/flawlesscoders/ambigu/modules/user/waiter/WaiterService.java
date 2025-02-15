package com.flawlesscoders.ambigu.modules.user.waiter;

import java.io.IOException;
import java.util.Base64;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.flawlesscoders.ambigu.modules.user.waiter.DTO.GetWaiterDTO;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WaiterService {

    private final WaiterRepository waiterRepository;

    private GetWaiterDTO toGetWaiterDTO(Waiter waiter) {
        return GetWaiterDTO.builder()
            .id(waiter.getId())
            .name(waiter.getName())
            .lastname_p(waiter.getLastname_p())
            .lastname_m(waiter.getLastname_m())
            .email(waiter.getEmail())
            .isLeader(waiter.isLeader())
            .shift(waiter.getShift())
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

    public ResponseEntity<Waiter> createWaiter(@Validated @RequestPart("waiter") Waiter waiter, @RequestPart("avatar") MultipartFile avatar) {
        try {
            if (!avatar.isEmpty()) {
                String base64Image = Base64.getEncoder().encodeToString(avatar.getBytes());
                waiter.setAvatarBase64(base64Image);
            }
            Waiter savedWaiter = waiterRepository.save(waiter);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedWaiter);
        }catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error al guardar la imagen");
        }
    }

    public ResponseEntity<Void> updateWaiter(@Valid Waiter waiter) {
        Waiter existingWaiter = waiterRepository.findById(waiter.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Mesero no encontrado"));

        existingWaiter.setName(waiter.getName());
        existingWaiter.setLastname_p(waiter.getEmail());
        existingWaiter.setLastname_m(waiter.getPassword());
        existingWaiter.setShift(waiter.getShift());
        existingWaiter.setEmail(waiter.getEmail());
        
        waiterRepository.save(existingWaiter);
        return ResponseEntity.ok().build();
    }

    public ResponseEntity<Void> updateWaiterAvatar (String id, MultipartFile avatar) {
        try {
            Waiter existingWaiter = waiterRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Mesero no encontrado"));

            String base64Image = Base64.getEncoder().encodeToString(avatar.getBytes());
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
    
}
