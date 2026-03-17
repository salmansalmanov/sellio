package com.sellio.controller;

import com.sellio.model.dto.request.ShopUpdateRequest;
import com.sellio.model.dto.response.core.ShopDetailsResponse;
import com.sellio.model.dto.response.core.ShopResponse;
import com.sellio.model.result.DataResult;
import com.sellio.model.result.PageData;
import com.sellio.model.result.Result;
import com.sellio.service.abstraction.ShopService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/shops")
@Tag(name = "Shop Controller", description = "Shop APIs")
public class ShopController {
    private final ShopService shopService;

    @GetMapping
    @Operation(summary = "Get all shops with pagination")
    public ResponseEntity<DataResult<PageData<ShopResponse>>> getAllShops(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return new ResponseEntity<>(shopService.getAllShops(page, size), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get shop by ID")
    public ResponseEntity<DataResult<ShopDetailsResponse>> getShopById(@PathVariable UUID id) {
        return new ResponseEntity<>(shopService.getShopById(id), HttpStatus.OK);
    }

    @Operation(summary = "Update shop by ID")
    @PutMapping(
            value = "/{id}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<DataResult<ShopDetailsResponse>> updateShopById(
            @PathVariable UUID id,
            @RequestPart("data") @Valid ShopUpdateRequest shopUpdateRequest,
            @RequestPart(value = "logo", required = false) MultipartFile logo,
            @RequestPart(value = "banner", required = false) MultipartFile banner
    ) throws IOException {
        return new ResponseEntity<>(shopService.updateShopById(id, shopUpdateRequest, logo, banner), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete shop by ID")
    public ResponseEntity<Result> deleteShopById(@PathVariable UUID id) {
        return new ResponseEntity<>(shopService.deleteShopById(id), HttpStatus.OK);
    }
}
