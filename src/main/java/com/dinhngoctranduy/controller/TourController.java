package com.dinhngoctranduy.controller;

import com.dinhngoctranduy.model.dto.TourRequestDTO;
import com.dinhngoctranduy.model.dto.TourResponseDTO;
import com.dinhngoctranduy.model.dto.TourSearchRequest;
import com.dinhngoctranduy.service.TourService;
import com.dinhngoctranduy.util.HtmlBuilder;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;


@RestController
@RequestMapping("/api/v1/tours")
@RequiredArgsConstructor
public class TourController {

    private final TourService tourService;

    @GetMapping
    public ResponseEntity<Page<TourResponseDTO>> getAllTours(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<TourResponseDTO> tours = tourService.getAllTours(page, size, false);
        return ResponseEntity.ok(tours);
    }

    @PostMapping
    public TourResponseDTO createTour(@RequestBody @Valid TourRequestDTO dto) {
        return tourService.createTour(dto);
    }

    @PutMapping("/{id}")
    public TourResponseDTO updateTour(@PathVariable Long id, @RequestBody @Valid TourRequestDTO dto) {
        return tourService.updateTour(id, dto);
    }

    @DeleteMapping("/{id}")
    public void deleteTour(@PathVariable Long id) {
        tourService.deleteTour(id);
    }

    @GetMapping("/{id}")
    public TourResponseDTO getTour(@PathVariable Long id) {
        return tourService.getById(id, false);
    }

    @GetMapping("/export/pdf")
    public ResponseEntity<byte[]> exportToursToPdf( @RequestParam(defaultValue = "0") int page,
                                                    @RequestParam(defaultValue = "10000") int size) {
        byte[] pdfBytes = tourService.exportToursToPdf(page, size);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.builder("inline")
                .filename(String.format("tours_%d.pdf", System.currentTimeMillis()))
                .build());

        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }



    @GetMapping("/export/excel")
    public ResponseEntity<byte[]> exportToursToExcel(@RequestParam(defaultValue = "0") int page,
                                                     @RequestParam(defaultValue = "10000") int size) throws IOException {
        ByteArrayInputStream in = tourService.exportToursToExcel(page, size);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=" + String.format("tours_%d.xlsx", System.currentTimeMillis()));

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(in.readAllBytes());
    }

    @GetMapping("/{id}/export/pdf")
    public ResponseEntity<byte[]> exportTourToPdf(@PathVariable Long id) {
        TourResponseDTO tour = tourService.getById(id, false); // trả về DTO
        String html = HtmlBuilder.buildTourHtml(tour);
        byte[] pdfBytes = exportHtmlToPdf(html);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=tour_" + tour.getCode() + ".pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }

    @PostMapping("/search")
    public List<TourResponseDTO> searchTours(@RequestBody TourSearchRequest request) {
        return tourService.searchTours(request, false);
    }

    public byte[] exportHtmlToPdf(String html) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            builder.withHtmlContent(html, null);
            builder.useFont(() -> getClass().getResourceAsStream("/fonts/ARIAL.TTF"), "Arial");
            builder.toStream(out);
            builder.run();
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to export PDF", e);
        }
    }

}
