package com.sellio.controller;

import com.sellio.model.dto.request.AdminInviteRequest;
import com.sellio.model.dto.request.AdminUpdateRequest;
import com.sellio.model.dto.response.core.AdminDetailsResponse;
import com.sellio.model.dto.response.core.AdminResponse;
import com.sellio.model.result.DataResult;
import com.sellio.model.result.PageData;
import com.sellio.model.result.Result;
import com.sellio.model.result.SuccessResult;
import com.sellio.service.abstraction.AdminService;
import io.swagger.v3.oas.annotations.Operation;
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
    @Operation(summary = "Admin Invite API")
    public ResponseEntity<Result> inviteAdmin(@RequestBody @Valid AdminInviteRequest request) {
        adminService.invite(request);
        return ResponseEntity.ok(new SuccessResult("Admin invited successfully"));
    }

    @GetMapping
    @Operation(summary = "Get all admins")
    public ResponseEntity<DataResult<PageData<AdminResponse>>> getAllAdmins(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return new ResponseEntity<>(adminService.getAllAdmins(page, size), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get admin by ID")
    public ResponseEntity<DataResult<AdminDetailsResponse>> getAdminById(@PathVariable UUID id) {
        return new ResponseEntity<>(adminService.getAdminById(id), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update admin by ID")
    public ResponseEntity<DataResult<AdminDetailsResponse>> updateAdminById(
            @PathVariable UUID id,
            @RequestBody @Valid AdminUpdateRequest request
    ) {
        return new ResponseEntity<>(adminService.updateAdminById(id, request), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete admin by ID")
    public ResponseEntity<Result> deleteAdminById(@PathVariable UUID id) {
        return new ResponseEntity<>(adminService.deleteAdminById(id), HttpStatus.OK);
    }
}
