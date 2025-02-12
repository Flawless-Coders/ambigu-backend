package com.flawlesscoders.ambigu.menu;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "menus")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Model that represents the predefined menus in a restaurant, allowing dishes to be added or removed.")
public class Menu {
    @Id
    private String id;

    @NotBlank(message = "El nombre del menú es obligatorio.")
    private String name;

    @NotBlank(message = "La descripción del menú es obligatoria.")
    @Schema(description = "Brief description of the menu.")
    private String description;

    @Schema(description = "Indicates whether a menu is enabled or disabled.")
    private boolean status;

    @Schema(description = "List containing the identifiers of the dishes included in the menu.")
    private List<String> dishes;
    
}
