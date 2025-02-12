package com.flawlesscoders.ambigu.category;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "categories")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Model that represents the dish categories in the restaurant.")
public class Category {
    @Id
    private String id;

    @NotBlank(message = "El nombre de la categoría es obligatorio.")
    private String name;

    @NotBlank(message = "La imagen de la categoría es obligatoria.")
    @Schema(description = "URL of the category image.")
    private String image;

    @Schema(description = "Indicates whether a category is enabled or disabled.")
    private boolean status;
}