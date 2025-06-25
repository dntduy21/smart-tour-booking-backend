package com.dinhngoctranduy.controller;


import com.dinhngoctranduy.model.dto.CustomTourReplyRequest;
import com.dinhngoctranduy.model.dto.CustomTourRequest;
import com.dinhngoctranduy.model.dto.CustomTourResponse;
import com.dinhngoctranduy.service.CustomTourService;
import com.dinhngoctranduy.util.SuccessPayload;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    public ResponseEntity<List<CustomTourResponse>> getAllCustomTours(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "1000") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        List<CustomTourResponse> responses = customTourService.getAllCustomTours(pageable);
        return ResponseEntity.ok(responses);
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

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> softDeleteCustomTour(@PathVariable Long id) {
        customTourService.softDeleteCustomTour(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<CustomTourResponse> updateCustomTourStatus(@PathVariable Long id, @RequestParam boolean status) {
        CustomTourResponse response = customTourService.updateCustomTourStatus(id, status);
        return ResponseEntity.ok(response);
    }

}
