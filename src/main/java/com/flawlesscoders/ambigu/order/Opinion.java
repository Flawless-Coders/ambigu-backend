package com.flawlesscoders.ambigu.order;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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
    @Size(min = 1, message = "La calificación no puede quedar vacía")
    private int rating;

    @NotBlank(message = "Se debe de agregar un comentario acerca de su experiencia")
    private String comment;
}
