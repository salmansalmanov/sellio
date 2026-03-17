package com.sellio.controller;

import com.sellio.model.dto.request.CategoryCreateRequest;
import com.sellio.model.dto.request.CategoryUpdateRequest;
import com.sellio.model.dto.response.core.CategoryDetailsResponse;
import com.sellio.model.dto.response.core.CategoryResponse;
import com.sellio.model.result.DataResult;
import com.sellio.model.result.PageData;
import com.sellio.model.result.Result;
import com.sellio.service.abstraction.CategoryService;
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
@RequestMapping("/v1/categories")
@Tag(name = "Category Controller", description = "Category APIs")
public class CategoryController {
    private final CategoryService categoryService;

    @PostMapping
    @Operation(summary = "Create category")
    public ResponseEntity<DataResult<CategoryDetailsResponse>> createCategory(@RequestBody @Valid CategoryCreateRequest request) {
        return new ResponseEntity<>(categoryService.save(request), HttpStatus.OK);
    }

    @GetMapping
    @Operation(summary = "Get all categories")
    public ResponseEntity<DataResult<PageData<CategoryResponse>>>  getAllCategories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return new ResponseEntity<>(categoryService.getAll(page, size), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get category by ID")
    public ResponseEntity<DataResult<CategoryDetailsResponse>> getCategoryById(@PathVariable UUID id) {
        return new ResponseEntity<>(categoryService.getById(id), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update category by ID")
    public ResponseEntity<DataResult<CategoryDetailsResponse>> updateCategoryById(@PathVariable UUID id, @RequestBody @Valid CategoryUpdateRequest request) {
        return new ResponseEntity<>(categoryService.updateById(id, request), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete category by ID")
    public ResponseEntity<Result> deleteCategoryById(@PathVariable UUID id) {
        return new ResponseEntity<>(categoryService.deleteById(id), HttpStatus.OK);
    }
}
