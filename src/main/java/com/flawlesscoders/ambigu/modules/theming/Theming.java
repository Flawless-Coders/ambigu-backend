package com.flawlesscoders.ambigu.modules.theming;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "theming")
@Schema(description = "Model that represents the theming of the system")
public class Theming {
    @Id
    private String id;
    @Schema(description = "Primary color of the system", example = "#000000")
    private String primaryColor;
    @Schema(description = "Secondary color of the system", example = "#000000")
    private String secondaryColor;
    @Schema(description = "Background color of the system", example = "#000000")
    private String backgroundColor;
    @Schema(description = "Header font of the system", example = "Arial")
    private String headerFont;
    @Schema(description = "Body font of the system", example = "Arial")
    private String bodyFont;
    @Schema(description = "Paragraph font of the system", example = "Arial")
    private String paragraphFont;
    @Schema(description = "Logo of the system")
    private String logo;
    @Schema(description = "Small logo of the system")
    private String logoSmall;
}
