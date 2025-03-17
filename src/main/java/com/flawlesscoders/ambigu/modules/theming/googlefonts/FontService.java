package com.flawlesscoders.ambigu.modules.theming.googlefonts;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.flawlesscoders.ambigu.modules.theming.googlefonts.DTO.GoogleFontItem;
import com.flawlesscoders.ambigu.modules.theming.googlefonts.DTO.GoogleFontsResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FontService {
    private final FontRepository fontRepository;

    @Value("${google.fonts.api.url}")
    private String googleFontsApiUrl;

    private static final String GOOGLE_FONTS_ID = "google_fonts";

    public ResponseEntity<Fonts> getFonts() {
        Fonts fonts = fontRepository.findById(GOOGLE_FONTS_ID)
                .orElseGet(() -> {
                    Fonts newFonts = updateFontsFromGoogle();
                    return newFonts != null ? newFonts: new Fonts();
                });
        return ResponseEntity.ok(fonts);
    }

    public Fonts updateFontsFromGoogle() {
        RestTemplate restTemplate = new RestTemplate();
        String url = googleFontsApiUrl;
        ResponseEntity<GoogleFontsResponse> response = restTemplate.getForEntity(url, GoogleFontsResponse.class);

        if (response.getBody() != null) {
            List<String> fontList = response.getBody().getItems()
                    .stream()
                    .map(GoogleFontItem::getFamily)
                    .collect(Collectors.toList());

            // Guardar en MongoDB
            Fonts fonts = new Fonts();
            fonts.setFonts(fontList);
            fontRepository.save(fonts);

            return fonts;
        }
        return null;
    }

}
