package com.dinhngoctranduy.controller;


import com.dinhngoctranduy.model.dto.CustomTourReplyRequest;
import com.dinhngoctranduy.model.dto.CustomTourRequest;
import com.dinhngoctranduy.model.dto.CustomTourResponse;
import com.dinhngoctranduy.service.CustomTourService;
import com.dinhngoctranduy.util.SuccessPayload;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tour/custom")
@AllArgsConstructor
public class CustomTourController {
    private final CustomTourService customTourService;
    @PostMapping
    public ResponseEntity<CustomTourResponse> createCustomTour(@RequestBody @Valid CustomTourRequest request) {
        CustomTourResponse response = customTourService.createCustomTour(request);
        return ResponseEntity.ok(response);
    }

    // GET all tours
    @GetMapping
//    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<List<CustomTourResponse>> getAllCustomTours() {
        return ResponseEntity.ok(customTourService.getAllCustomTours());
    }

    // GET tour by ID
    @GetMapping("/{id}")
    public ResponseEntity<CustomTourResponse> getCustomTourById(@PathVariable Long id) {
        return ResponseEntity.ok(customTourService.getCustomTourById(id, false));
    }

    @PatchMapping("/{id}/reply")
    public ResponseEntity<?> sendReplyToCustomer(
            @PathVariable Long id,
            @RequestBody @Valid CustomTourReplyRequest request) {
        customTourService.sendReplyToCustomer(id, request.getMessage());
        return ResponseEntity.ok(SuccessPayload.build());
    }

}
