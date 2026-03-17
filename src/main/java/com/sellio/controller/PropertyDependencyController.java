package com.sellio.controller;

import com.sellio.model.dto.request.PropertyDependencyCreateRequest;
import com.sellio.model.dto.response.core.PropertyDependencyDetailsResponse;
import com.sellio.model.dto.response.core.PropertyDependencyResponse;
import com.sellio.model.result.DataResult;
import com.sellio.model.result.PageData;
import com.sellio.model.result.Result;
import com.sellio.service.abstraction.PropertyDependencyService;
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
@RequestMapping("/v1/property-dependencies")
@Tag(name = "Property Dependency Controller", description = "Property Dependency APIs")
public class PropertyDependencyController {
    private final PropertyDependencyService propertyDependencyService;

    @PostMapping
    @Operation(summary = "Create property dependency")
    public ResponseEntity<DataResult<PropertyDependencyDetailsResponse>> createPropertyDependency(@RequestBody @Valid PropertyDependencyCreateRequest propertyDependencyCreateRequest) {
        return new ResponseEntity<>(propertyDependencyService.save(propertyDependencyCreateRequest), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get property dependency by ID")
    public ResponseEntity<DataResult<PropertyDependencyResponse>> getPropertyDependencyById(@PathVariable UUID id) {
        return new ResponseEntity<>(propertyDependencyService.getById(id), HttpStatus.OK);
    }

    @GetMapping
    @Operation(summary = "Get all property dependencies")
    public ResponseEntity<DataResult<PageData<PropertyDependencyResponse>>> getAllPropertyDependencies(
            @RequestParam(required = false) UUID parentPropertyValueId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return new ResponseEntity<>(propertyDependencyService.getAll(parentPropertyValueId, page, size), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete property dependency by ID")
    public ResponseEntity<Result> deletePropertyDependency(@PathVariable UUID id) {
        return new ResponseEntity<>(propertyDependencyService.deleteById(id), HttpStatus.OK);
    }
}
