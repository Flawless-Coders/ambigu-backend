package com.flawlesscoders.ambigu.modules.theming;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import io.micrometer.core.ipc.http.HttpSender.Response;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ThemingService {
    private final ThemingRepository themingRepository;

    public ResponseEntity<Theming> getTheming() {
        Theming theme = themingRepository.find();
        return ResponseEntity.ok(theme);
    }

    public ResponseEntity<Map<String, String>> getColors() {
        Theming theming = themingRepository.findAll().get(0);
        Map<String, String> colors = new HashMap<>();
        colors.put("primaryColor", theming.getPrimaryColor());
        colors.put("secondaryColor", theming.getSecondaryColor());
        colors.put("backgroundColor", theming.getBackgroundColor());
        return ResponseEntity.ok(colors);
    }

    public ResponseEntity<Void> updateColors(Theming theming) {
        try{
            Theming existingTheme = themingRepository.find();
            existingTheme.setPrimaryColor(theming.getPrimaryColor());
            existingTheme.setSecondaryColor(theming.getSecondaryColor());
            existingTheme.setBackgroundColor(theming.getBackgroundColor());
            themingRepository.save(existingTheme);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error updating colors");
        }
    }

    public ResponseEntity<Map<String, String>> getFonts() {
        Theming theming = themingRepository.findAll().get(0);
        Map<String, String> fonts = new HashMap<>();
        fonts.put("headerFont", theming.getHeaderFont());
        fonts.put("bodyFont", theming.getBodyFont());
        fonts.put("paragraphFont", theming.getParagraphFont());
        return ResponseEntity.ok(fonts);
    }

    public ResponseEntity<Void> updateFonts(Theming theming) {
        System.out.println(theming);
        try{
            Theming existingTheme = themingRepository.find();
            existingTheme.setHeaderFont(theming.getHeaderFont());
            existingTheme.setBodyFont(theming.getBodyFont());
            existingTheme.setParagraphFont(theming.getParagraphFont());
            themingRepository.save(existingTheme);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error updating fonts");
        }
    }

    public ResponseEntity<Map<String, String>> getLogos() {
        Theming theming = themingRepository.findAll().get(0);
        Map<String, String> logos = new HashMap<>();
        logos.put("logo", theming.getLogo());
        logos.put("logoSmall", theming.getLogoSmall());
        return ResponseEntity.ok(logos);
    }

    public ResponseEntity<Void> updateLogos(Theming theming) {
        try{
            Theming existingTheme = themingRepository.find();
            existingTheme.setLogo(theming.getLogo());
            existingTheme.setLogoSmall(theming.getLogoSmall());
            themingRepository.save(existingTheme);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error updating logos");
        }
    }
}
