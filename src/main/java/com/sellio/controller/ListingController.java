package com.sellio.controller;

import com.sellio.model.dto.request.ListingCreateRequest;
import com.sellio.model.dto.request.ListingUpdateRequest;
import com.sellio.model.dto.response.core.ListingDetailsResponse;
import com.sellio.model.dto.response.core.ListingResponse;
import com.sellio.model.result.DataResult;
import com.sellio.model.result.PageData;
import com.sellio.model.result.Result;
import com.sellio.service.abstraction.ListingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/listings")
@Tag(name = "Listing Controller", description = "Listing APIs")
public class ListingController {
    private final ListingService listingService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "Create listing",
            responses = {
                    @ApiResponse(description = "Success", responseCode = "201")
            }
    )
    public ResponseEntity<DataResult<ListingDetailsResponse>> createListing(
            @RequestPart("data") @Valid ListingCreateRequest request,
            @RequestPart("images") List<MultipartFile> images
    ) throws IOException {
        return new ResponseEntity<>(listingService.save(request, images), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get listing by ID",
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200")
            }
    )
    public ResponseEntity<DataResult<ListingDetailsResponse>> getListingById(@PathVariable UUID id) {
        return new ResponseEntity<>(listingService.getById(id), HttpStatus.OK);
    }

    @GetMapping
    @Operation(
            summary = "Get all listings",
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200")
            }
    )
    public ResponseEntity<DataResult<PageData<ListingResponse>>> getAllListings(
            @RequestParam(required = false) UUID ownerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return new ResponseEntity<>(listingService.getAll(ownerId, page, size), HttpStatus.OK);
    }

    @PutMapping(
            value = "/{id}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @Operation(
            summary = "Update listing by ID",
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200")
            }
    )
    public ResponseEntity<DataResult<ListingDetailsResponse>> updateListingById(
            @PathVariable UUID id,
            @RequestPart("data") @Valid ListingUpdateRequest request,
            @RequestPart("images") List<MultipartFile> images
    ) throws IOException {
        return new ResponseEntity<>(listingService.update(id, request, images), HttpStatus.OK);
    }

    @PatchMapping("/deactivate/{id}")
    @Operation(
            summary = "Delete listing by ID",
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200")
            }
    )
    public ResponseEntity<DataResult<ListingDetailsResponse>> deactivateListingById(@PathVariable UUID id) {
        return new ResponseEntity<>(listingService.deactivate(id), HttpStatus.OK);
    }

    @PatchMapping("/activate/{id}")
    @Operation(
            summary = "Activate listing by ID",
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200")
            }
    )
    public ResponseEntity<DataResult<ListingDetailsResponse>> activateListingById(@PathVariable UUID id) {
        return new ResponseEntity<>(listingService.activate(id), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete listing by ID",
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200")
            }
    )
    public ResponseEntity<Result> deleteListingById(@PathVariable UUID id) {
        return new ResponseEntity<>(listingService.delete(id), HttpStatus.OK);
    }
}
