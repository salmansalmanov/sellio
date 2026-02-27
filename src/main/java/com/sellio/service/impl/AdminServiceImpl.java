package com.sellio.service.impl;

import com.sellio.model.dto.request.RegisterRequest;
import com.sellio.model.dto.response.core.UserResponse;
import com.sellio.model.result.DataResult;
import com.sellio.service.abstraction.AdminService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class AdminServiceImpl implements AdminService {
    @Override
    public DataResult<UserResponse> save(RegisterRequest registerRequest, MultipartFile logo, MultipartFile banner) {
        return null;
    }
}
