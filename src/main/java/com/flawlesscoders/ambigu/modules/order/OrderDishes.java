package com.flawlesscoders.ambigu.modules.order;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Model that repsents the base opinion of the order")
public class OrderDishes {
    @NotNull(message = "El ID del platillo no puede ser nulo")
    @Schema(description = "Dish's ID for the order")
    private String dishId;

    @NotNull(message = "La cantidad del platillo no puede ser nula")
    @Schema(description = "Quantity of the dish")
    private int quantity;

    @NotNull(message = "El precio unitario no puede ser nulo")
    @Schema(description = "Unit price of the dish")
    private float unitPrice;

    @Schema(description = "Comment of the dish")
    private String comment;
}
