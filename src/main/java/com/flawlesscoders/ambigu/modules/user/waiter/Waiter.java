package com.flawlesscoders.ambigu.modules.user.waiter;

import org.springframework.data.mongodb.core.mapping.Document;

import com.flawlesscoders.ambigu.modules.user.base.User;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Document(collection = "users")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@Schema(description = "Model that represents the waiter user")
public class Waiter extends User {

    @Schema(description = "Used to know if the waiter is the leader of the shift")
    @NotNull(message = "El estatus de lider es requerido")
    private boolean isLeader;

    @Schema(description = "Average rating of the waiter")
    private float avgRating;
}
