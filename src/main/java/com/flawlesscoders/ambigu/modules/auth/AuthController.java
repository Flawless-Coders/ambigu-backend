package com.flawlesscoders.ambigu.modules.auth;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.flawlesscoders.ambigu.modules.auth.BlacklistToken.BlacklistToken;
import com.flawlesscoders.ambigu.modules.auth.BlacklistToken.BlacklistTokenRepository;
import com.flawlesscoders.ambigu.utils.security.JwtTokenProvider;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    private final BlacklistTokenRepository blacklistTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Operation(summary ="Login with platform restriction")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User logged in"),
        @ApiResponse(responseCode = "400", description = "Invalid credentials"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
    })
    @PostMapping("/login")
    public ResponseEntity<Map<String,String>> login(@Validated @RequestBody AuthRequest request) {

        String token = authService.login(request);

        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        return ResponseEntity.ok(response);

    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);

        Date expirationDate = jwtTokenProvider.getExpirationDateFromToken(token);

        blacklistTokenRepository.save(new BlacklistToken(token, expirationDate));

        return ResponseEntity.ok("Logout exitoso");
    }
}
