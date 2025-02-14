package com.flawlesscoders.ambigu.modules.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Model that represents the authentication request")
public class AuthRequest {

    @Email
    @NotBlank(message = "El email es obligatorio")
    @Schema(description = "User's email")
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    @Schema(description = "User's password")
    private String password;

    @Schema(description = "Platform from where the user is trying to login", example = "WEB, MOBILE")
    private String platform;
}
