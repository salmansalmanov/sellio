package com.sellio.controller;

import com.sellio.model.dto.request.SubcategoryCreateRequest;
import com.sellio.model.dto.request.SubcategoryUpdateRequest;
import com.sellio.model.dto.response.core.SubcategoryDetailsResponse;
import com.sellio.model.dto.response.core.SubcategoryResponse;
import com.sellio.model.result.DataResult;
import com.sellio.model.result.PageData;
import com.sellio.model.result.Result;
import com.sellio.service.abstraction.SubcategoryService;
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
@RequestMapping("/v1/subcategories")
@Tag(name = "Subcategory Controller", description = "Subcategory APIs")
public class SubcategoryController {
    private final SubcategoryService subcategoryService;

    @PostMapping
    @Operation(summary = "Create subcategory")
    public ResponseEntity<DataResult<SubcategoryDetailsResponse>> createSubcategory(@RequestBody @Valid SubcategoryCreateRequest request) {
        return new ResponseEntity<>(subcategoryService.save(request), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get subcategory by ID")
    public ResponseEntity<DataResult<SubcategoryDetailsResponse>> getSubcategory(@PathVariable UUID id) {
        return new ResponseEntity<>(subcategoryService.getById(id), HttpStatus.OK);
    }

    @GetMapping
    @Operation(summary = "Get all subcategories")
    public ResponseEntity<DataResult<PageData<SubcategoryResponse>>> getAllSubcategories(
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return new ResponseEntity<>(subcategoryService.getAll(page, size, categoryId), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update subcategory by ID")
    public ResponseEntity<DataResult<SubcategoryDetailsResponse>> updateSubcategory(
            @PathVariable UUID id,
            @RequestBody @Valid SubcategoryUpdateRequest request
    ) {
        return new ResponseEntity<>(subcategoryService.update(id, request), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete subcategory by ID")
    public ResponseEntity<Result> deleteSubcategory(@PathVariable UUID id) {
        return new ResponseEntity<>(subcategoryService.delete(id), HttpStatus.OK);
    }
}
