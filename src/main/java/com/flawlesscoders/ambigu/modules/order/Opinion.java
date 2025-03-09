package com.flawlesscoders.ambigu.modules.order;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
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
public class Opinion {
    @NotNull(message = "La calificación no puede estar vacía")
    @Min(value = 1, message = "La calificación no puede ser menor a 1")
    private Integer qualification;

    @NotNull(message = "El comentario no puede estar vacío")
    @NotEmpty(message = "El comentario no puede estar vacío")
    private String comment;
}
