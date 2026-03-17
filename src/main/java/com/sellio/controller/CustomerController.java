package com.sellio.controller;

import com.sellio.model.dto.request.CustomerUpdateRequest;
import com.sellio.model.dto.response.core.CustomerDetailsResponse;
import com.sellio.model.dto.response.core.CustomerResponse;
import com.sellio.model.result.DataResult;
import com.sellio.model.result.PageData;
import com.sellio.model.result.Result;
import com.sellio.service.abstraction.CustomerService;
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
@RequestMapping("/v1/customers")
@Tag(name = "Customer Controller", description = "Customer APIs")
public class CustomerController {
    private final CustomerService customerService;

    @GetMapping
    @Operation(summary = "Get all customers")
    public ResponseEntity<DataResult<PageData<CustomerResponse>>> getAllCustomers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return new ResponseEntity<>(customerService.getAllCustomers(page, size), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get customer by ID")
    public ResponseEntity<DataResult<CustomerDetailsResponse>> getCustomerById(@PathVariable UUID id) {
        return new ResponseEntity<>(customerService.getCustomerById(id), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update customer by ID")
    public ResponseEntity<DataResult<CustomerDetailsResponse>> updateCustomerById(
            @PathVariable UUID id,
            @RequestBody @Valid CustomerUpdateRequest request
    ) {
        return new ResponseEntity<>(customerService.updateCustomerById(id, request), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete customer by ID")
    public ResponseEntity<Result> deleteCustomerById(@PathVariable UUID id) {
        return new ResponseEntity<>(customerService.deleteCustomerById(id), HttpStatus.OK);
    }
}
