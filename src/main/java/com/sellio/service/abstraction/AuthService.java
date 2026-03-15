package com.sellio.service.abstraction;

import com.sellio.model.dto.request.LoginRequest;
import com.sellio.model.dto.request.RegisterRequest;
import com.sellio.model.dto.request.TokenRefreshRequest;
import com.sellio.model.dto.response.core.LoginResponse;
import com.sellio.model.dto.response.core.TokenRefreshResponse;
import com.sellio.model.dto.response.core.UserResponse;
import com.sellio.model.result.DataResult;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface AuthService {
    DataResult<UserResponse> register(RegisterRequest registerRequest, String role, MultipartFile logo, MultipartFile banner) throws IOException;

    DataResult<LoginResponse> login(LoginRequest loginRequest);

    DataResult<TokenRefreshResponse> refreshToken(TokenRefreshRequest request);
}
