package com.sellio.service.abstraction;

import com.sellio.model.dto.request.RegisterRequest;
import com.sellio.model.dto.response.core.UserResponse;
import com.sellio.model.result.DataResult;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface UserService {
    DataResult<UserResponse> save(RegisterRequest registerRequest, MultipartFile logo, MultipartFile banner) throws IOException;
}
