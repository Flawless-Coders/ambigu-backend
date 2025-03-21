package com.flawlesscoders.ambigu.modules.theming.googlefonts;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Document(collection = "fonts")
@Data
public class Fonts {
    @Id
    private String id = "google_fonts";
    private List<String> fonts;
    private int totalFonts;
    
}
