package com.sellio.controller;

import com.sellio.model.dto.request.PropertyCreateRequest;
import com.sellio.model.dto.request.PropertyUpdateRequest;
import com.sellio.model.dto.response.core.PropertyDetailsResponse;
import com.sellio.model.dto.response.core.PropertyResponse;
import com.sellio.model.result.DataResult;
import com.sellio.model.result.PageData;
import com.sellio.model.result.Result;
import com.sellio.service.abstraction.PropertyService;
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
@RequestMapping("/v1/properties")
@Tag(name = "Property Controller", description = "Property APIs")
public class PropertyController {
    private final PropertyService propertyService;

    @PostMapping
    @Operation(summary = "Create property")
    public ResponseEntity<DataResult<PropertyDetailsResponse>> createProperty(@RequestBody @Valid PropertyCreateRequest request) {
        return new ResponseEntity<>(propertyService.save(request), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get property by ID")
    public ResponseEntity<DataResult<PropertyDetailsResponse>> getProperty(@PathVariable UUID id) {
        return new ResponseEntity<>(propertyService.getById(id), HttpStatus.OK);
    }

    @GetMapping
    @Operation(summary = "Get all properties")
    public ResponseEntity<DataResult<PageData<PropertyResponse>>> getAllProperties(
            @RequestParam(required = false) UUID subcategoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return new ResponseEntity<>(propertyService.getAll(subcategoryId, page, size), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update property by ID")
    public ResponseEntity<DataResult<PropertyDetailsResponse>> updateProperty(@PathVariable UUID id, @RequestBody @Valid PropertyUpdateRequest request) {
        return new ResponseEntity<>(propertyService.update(id, request), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete property by ID")
    public ResponseEntity<Result> deleteProperty(@PathVariable UUID id) {
        return new ResponseEntity<>(propertyService.delete(id), HttpStatus.OK);
    }
}
