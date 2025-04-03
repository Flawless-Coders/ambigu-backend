package com.flawlesscoders.ambigu.modules.theming;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/theming")
@RequiredArgsConstructor
public class ThemingController {
    private final ThemingService themingService;

    //GET THEMING
    @GetMapping
    public ResponseEntity<Theming> getTheming() {
        return themingService.getTheming();
    }

    //GET COLORS
    @GetMapping("/colors")
    public ResponseEntity<Map<String,String>> getColors() {
        return themingService.getColors();
    }

    //UPDATE COLORS
    @PatchMapping("/colors")
    public ResponseEntity<Void> updateColors(@RequestBody Theming theming) {
        return themingService.updateColors(theming);
    }

    //GET FONTS
    @GetMapping("/fonts")
    public ResponseEntity<Map<String,String>> getFonts() {
        return themingService.getFonts();
    }

    //UPDATE FONTS
    @PatchMapping("/fonts")
    public ResponseEntity<Void> updateFonts(@RequestBody Theming theming) {
        return themingService.updateFonts(theming);
    }

    //GET LOGOS
    @GetMapping("/logos")
    public ResponseEntity<Map<String,String>> getLogos() {
        return themingService.getLogos();
    }

    @PatchMapping(value = "/logos", consumes = {"multipart/form-data"} )
    public ResponseEntity<Void> updateLogos(@RequestPart("logo") MultipartFile logo, @RequestPart("logoSmall") MultipartFile logoSmall) {
        return themingService.updateLogos(logo, logoSmall);
    }

    //Apply the theme
    @PostMapping("/apply")
    public ResponseEntity<?> applyChanges(HttpServletRequest request) {
        return themingService.applyChanges(request.getSession().getId());
    }

    @GetMapping("/public-theme/default_theme")
    public ResponseEntity<Theming> getDefaultTheme() {
        return themingService.getDefaultTheme();
    }

    
}
