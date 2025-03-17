package com.flawlesscoders.ambigu.modules.theming.googlefonts;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/theming/getfonts")
@RequiredArgsConstructor
public class FontController {
    private final FontService fontService;

    @GetMapping
    public ResponseEntity<Fonts> getFonts() {
        return fontService.getFonts();
    }
}
