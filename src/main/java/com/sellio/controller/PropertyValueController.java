package com.sellio.controller;

import com.sellio.model.dto.request.PropertyValueAddRequest;
import com.sellio.model.dto.request.PropertyValueUpdateRequest;
import com.sellio.model.dto.response.core.PropertyValueGetResponse;
import com.sellio.model.dto.response.core.PropertyValueResponse;
import com.sellio.model.dto.response.core.PropertyValueSaveResponse;
import com.sellio.model.result.DataResult;
import com.sellio.model.result.PageData;
import com.sellio.model.result.Result;
import com.sellio.service.abstraction.PropertyValueService;
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
@RequestMapping("/v1/property-values")
@Tag(name = "Property Value Controller", description = "Property Value APIs")
public class PropertyValueController {
    private final PropertyValueService propertyValueService;

    @PostMapping
    @Operation(summary = "Create property value")
    public ResponseEntity<DataResult<PropertyValueSaveResponse>> createPropertyValue(@RequestBody @Valid PropertyValueAddRequest request) {
        return new ResponseEntity<>(propertyValueService.save(request), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get property value by ID")
    public ResponseEntity<DataResult<PropertyValueGetResponse>> getPropertyValueById(@PathVariable UUID id) {
        return new ResponseEntity<>(propertyValueService.getById(id), HttpStatus.OK);
    }

    @GetMapping
    @Operation(summary = "Get all property values")
    public ResponseEntity<DataResult<PageData<PropertyValueResponse>>> getAllPropertyValues(
            @RequestParam(required = false) UUID propertyId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return new ResponseEntity<>(propertyValueService.getAll(propertyId, page, size), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update property value by ID")
    public ResponseEntity<DataResult<PropertyValueGetResponse>> updatePropertyValueById(@PathVariable UUID id, @RequestBody @Valid PropertyValueUpdateRequest request) {
        return new ResponseEntity<>(propertyValueService.update(id, request), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete property value by ID")
    public ResponseEntity<Result> deletePropertyValueById(@PathVariable UUID id) {
        return new ResponseEntity<>(propertyValueService.delete(id), HttpStatus.OK);
    }
}
