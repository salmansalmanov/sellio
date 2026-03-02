package com.sellio.controller;

import com.sellio.model.dto.request.AdminInviteRequest;
import com.sellio.model.dto.request.AdminUpdateRequest;
import com.sellio.model.dto.response.core.AdminDetailsResponse;
import com.sellio.model.dto.response.core.AdminResponse;
import com.sellio.model.result.*;
import com.sellio.service.abstraction.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/admins")
@Tag(name = "Admin Controller", description = "Admin APIs")
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

    @GetMapping
    @Operation(
            description = "Get all admins",
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200")
            }
    )
    public ResponseEntity<DataResult<PageData<AdminResponse>>> getAllAdmins(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return new ResponseEntity<>(adminService.getAllAdmins(page, size), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Operation(
            description = "Get admin by ID",
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200")
            }
    )
    public ResponseEntity<DataResult<AdminDetailsResponse>> getAdminById(@PathVariable UUID id) {
        return new ResponseEntity<>(adminService.getAdminById(id), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    @Operation(
            description = "Update admin by ID",
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200")
            }
    )
    public ResponseEntity<DataResult<AdminDetailsResponse>> updateAdminById(
            @PathVariable UUID id,
            @RequestBody @Valid AdminUpdateRequest request
    ) {
        return new ResponseEntity<>(adminService.updateAdminById(id, request), HttpStatus.OK);
    }
}
