package com.sellio.controller;

import com.sellio.model.dto.request.CityCreateRequest;
import com.sellio.model.dto.request.CityUpdateRequest;
import com.sellio.model.dto.response.core.CityResponse;
import com.sellio.model.result.DataResult;
import com.sellio.model.result.PageData;
import com.sellio.model.result.Result;
import com.sellio.service.abstraction.CityService;
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
@RequestMapping("/v1/cities")
@Tag(name = "City Controller", description = "City APIs")
public class CityController {
    private final CityService cityService;

    @PostMapping
    @Operation(summary = "Create city")
    private ResponseEntity<DataResult<CityResponse>> createCity(@RequestBody @Valid CityCreateRequest request) {
        return new ResponseEntity<>(cityService.save(request), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get city by ID")
    private ResponseEntity<DataResult<CityResponse>> getCityById(@PathVariable UUID id) {
        return new ResponseEntity<>(cityService.getById(id), HttpStatus.OK);
    }

    @GetMapping
    @Operation(summary = "Get all cities")
    private ResponseEntity<DataResult<PageData<CityResponse>>> getAllCities(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return new ResponseEntity<>(cityService.getAll(page, size), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update city by ID")
    private ResponseEntity<DataResult<CityResponse>> updateCityById(@PathVariable UUID id, @RequestBody @Valid CityUpdateRequest request) {
        return new ResponseEntity<>(cityService.update(id, request), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete city by ID")
    private ResponseEntity<Result> deleteCityById(@PathVariable UUID id) {
        return new ResponseEntity<>(cityService.delete(id), HttpStatus.OK);
    }
}
