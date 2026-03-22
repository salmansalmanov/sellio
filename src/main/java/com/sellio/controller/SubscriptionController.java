package com.sellio.controller;

import com.sellio.model.enums.PricingPlan;
import com.sellio.model.result.Result;
import com.sellio.service.concrete.PricingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/pricing")
@Tag(name = "Subscription Controller", description = "Subscription API")
public class SubscriptionController {
    private final PricingService pricingService;

    @PatchMapping("/change-plan/{id}")
    @Operation(summary = "Change plan")
    public ResponseEntity<Result> changePlan(@RequestParam PricingPlan pricingPlan, @PathVariable UUID id) {
        return new ResponseEntity<>(pricingService.changePlan(pricingPlan, id), HttpStatus.OK);
    }
}
