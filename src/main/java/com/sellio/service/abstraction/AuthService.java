package com.sellio.service.abstraction;

import com.sellio.model.dto.request.RegisterRequest;
import com.sellio.model.dto.response.UserResponse;
import com.sellio.model.enums.Role;
import com.sellio.model.result.DataResult;
import org.springframework.web.multipart.MultipartFile;

public interface AuthService {
    DataResult<UserResponse> register(RegisterRequest registerRequest, String role, MultipartFile logo, MultipartFile banner);
}
