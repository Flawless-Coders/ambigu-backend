package com.flawlesscoders.ambigu.modules.dish;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "dishes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Model that represents individual dishes in the restaurant.")
public class Dish {
    @Id
    private String id;

    @NotBlank(message = "El nombre del platillo es obligatorio.")
    private String name;

    @NotBlank(message = "La descripción del platillo es obligatoria.")
    @Schema(description = "Brief description of the dish.")
    private String description;

    @NotBlank(message = "La categoría del platillo es obligatoria.")
    @Schema(description = "Identifier of the category to which the dish belongs.")
    private String category;

    @Schema(description = "Base64 representation of the dish image.")
    private String imageBase64;

    @Schema(description = "Indicates whether the dish is active or inactive.")
    private boolean status;

    private float price;
}