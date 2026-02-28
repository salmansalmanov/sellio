package com.sellio.controller;

import com.sellio.model.dto.request.AdminInviteRequest;
import com.sellio.model.result.Result;
import com.sellio.model.result.SuccessResult;
import com.sellio.service.abstraction.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/admins")
public class AdminController {
    private final AdminService adminService;

    @PostMapping("/invite")
    @Operation(
            description = "Admin Invite API",
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200")
            }
    )
    public ResponseEntity<Result> inviteAdmin(@RequestBody AdminInviteRequest request) {
        adminService.invite(request);
        return ResponseEntity.ok(new SuccessResult("Admin invited successfully"));
    }
}
