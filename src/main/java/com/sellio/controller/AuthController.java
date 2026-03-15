package com.sellio.controller;

import com.sellio.model.dto.request.LoginRequest;
import com.sellio.model.dto.request.RegisterRequest;
import com.sellio.model.dto.request.TokenRefreshRequest;
import com.sellio.model.dto.response.core.LoginResponse;
import com.sellio.model.dto.response.core.TokenRefreshResponse;
import com.sellio.model.dto.response.core.UserResponse;
import com.sellio.model.result.DataResult;
import com.sellio.service.abstraction.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/auth")
@Tag(name = "Auth Controller", description = "Auth APIs")
public class AuthController {
    private final AuthService authService;

    @PostMapping(
            value = "/register",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @Operation(
            summary = "Register API",
            responses = {
                    @ApiResponse(description = "Success", responseCode = "201")
            }
    )
    public ResponseEntity<DataResult<UserResponse>> register(
            @Valid RegisterRequest request,
            @RequestPart(value = "logo", required = false) MultipartFile logo,
            @RequestPart(value = "banner", required = false) MultipartFile banner,
            @RequestParam String role
    ) throws IOException {
        return new ResponseEntity<>(authService.register(request, role, logo, banner), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    @Operation(
            summary = "Login API",
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200")
            }
    )
    public ResponseEntity<DataResult<LoginResponse>> login(@RequestBody @Valid LoginRequest request) {
        return new ResponseEntity<>(authService.login(request), HttpStatus.OK);
    }

    @PostMapping("/refresh-token")
    @Operation(
            summary = "Refresh access and refresh token",
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200")
            }
    )
    public ResponseEntity<DataResult<TokenRefreshResponse>> refreshToken(@RequestBody @Valid TokenRefreshRequest request) {
        return new ResponseEntity<>(authService.refreshToken(request), HttpStatus.OK);
    }
}
