package com.dinhngoctranduy.service.impl;

import com.dinhngoctranduy.util.error.InvalidDataException;
import com.dinhngoctranduy.model.Image;
import com.dinhngoctranduy.model.Tour;
import com.dinhngoctranduy.model.dto.ImageDTO;
import com.dinhngoctranduy.model.request.TourRequestDTO;
import com.dinhngoctranduy.model.response.TourResponseDTO;
import com.dinhngoctranduy.model.request.TourSearchRequest;
import com.dinhngoctranduy.repository.TourRepository;
import com.dinhngoctranduy.repository.TourSpecification;
import com.dinhngoctranduy.service.TourService;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.UnitValue;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.TextAlignment;

import java.nio.file.Paths;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TourServiceImpl implements TourService {

    private final TourRepository tourRepository;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public TourResponseDTO createTour(TourRequestDTO dto) {
        if (tourRepository.existsByCode(dto.getCode())) {
            throw new InvalidDataException("Tour code already exists");
        }
        Tour tour = toEntity(dto);
        tour.setBookings(Collections.emptyList());
        return toDto(tourRepository.save(tour));
    }

    @Override
    public TourResponseDTO updateTour(Long id, TourRequestDTO dto) {
        validateTourRequest(dto);

        return tourRepository.findByIdAndDeletedFalse(id)
                .map(tour -> {
                    if (dto.getTitle() != null && !dto.getTitle().equals(tour.getTitle())) {
                        tour.setTitle(dto.getTitle());
                    }
                    if (dto.getDescription() != null && !dto.getDescription().equals(tour.getDescription())) {
                        tour.setDescription(dto.getDescription());
                    }
                    if (dto.getCapacity() != null && !dto.getCapacity().equals(tour.getCapacity())) {
                        tour.setCapacity(dto.getCapacity());
                    }
                    if (dto.getPriceAdults() != null && !dto.getPriceAdults().equals(tour.getPriceAdults())) {
                        tour.setPriceAdults(dto.getPriceAdults());
                    }
                    if (dto.getPriceChildren() != null && !dto.getPriceChildren().equals(tour.getPriceChildren())) {
                        tour.setPriceChildren(dto.getPriceChildren());
                    }
                    if (dto.getStartDate() != null && !dto.getStartDate().equals(tour.getStartDate())) {
                        tour.setStartDate(dto.getStartDate());
                    }
                    if (dto.getEndDate() != null && !dto.getEndDate().equals(tour.getEndDate())) {
                        tour.setEndDate(dto.getEndDate());
                    }
                    if (dto.getDestination() != null && !dto.getDestination().equals(tour.getDestination())) {
                        tour.setDestination(dto.getDestination());
                    }
                    if (dto.getRegion() != null && !dto.getRegion().equals(tour.getRegion())) {
                        tour.setRegion(dto.getRegion());
                    }
                    if (dto.getCategory() != null && !dto.getCategory().equals(tour.getCategory())) {
                        tour.setCategory(dto.getCategory());
                    }
                    if (dto.getAirline() != null && !dto.getAirline().equals(tour.getAirline())) {
                        tour.setAirline(dto.getAirline());
                    }
                    if (dto.getCode() != null && !dto.getCode().equals(tour.getCode())) {
                        tour.setCode(dto.getCode());
                    }
                    if (dto.getAvailable() != null && !dto.getAvailable() != tour.isAvailable()) {
                        tour.setAvailable(dto.getAvailable());
                    }
                    if (dto.getItinerary() != null && !dto.getItinerary().equals(tour.getItinerary())) {
                        tour.setItinerary(dto.getItinerary());
                    }

                    // Xử lý images, nếu có và khác
                    if (dto.getImages() != null) {
                        List<String> currentUrls = tour.getImages().stream()
                                .map(Image::getUrl)
                                .collect(Collectors.toList());
                        if (!dto.getImages().equals(currentUrls)) {
                            tour.getImages().clear();
                            List<Image> newImages = dto.getImages().stream()
                                    .map(url -> Image.builder()
                                            .url(url)
                                            .uploadedAt(Instant.now())
                                            .tour(tour)
                                            .build())
                                    .collect(Collectors.toList());
                            tour.getImages().addAll(newImages);
                        }
                    }

                    return toDto(tourRepository.save(tour));
                })
                .orElseThrow(() -> new RuntimeException("Tour not found"));
    }

    @Override
    public void deleteTour(Long id) {
        Tour tour = tourRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new RuntimeException("Tour not found"));
        tour.setDeleted(true);
        tourRepository.save(tour);
    }

    @Override
    public TourResponseDTO getById(Long id, Boolean isDeleted) {
        return (isDeleted != null ? tourRepository.findByIdAndDeletedFalse(id) : tourRepository.findById(id))
                .map(this::toDto)
                .orElseThrow(() -> new RuntimeException("Tour not found"));
    }

    @Override
    public List<TourResponseDTO> getAll(Boolean isDeleted) {
        return (isDeleted != null ? tourRepository.findAll() : tourRepository.findAllByDeletedFalse())
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private Tour toEntity(TourRequestDTO dto) {
        Tour tour = Tour.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .capacity(dto.getCapacity())
                .priceAdults(dto.getPriceAdults())
                .priceChildren(dto.getPriceChildren())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .destination(dto.getDestination())
                .region(dto.getRegion())
                .category(dto.getCategory())
                .airline(dto.getAirline())
                .code(dto.getCode())
                .available(dto.getAvailable() != null && dto.getAvailable())
                .itinerary(dto.getItinerary())
                .images(
                        dto.getImages().stream()
                                .map(url ->
                                        Image.builder()
                                                .url(url)
                                                .uploadedAt(Instant.now())
                                                .build()
                                ).collect(Collectors.toList())
                )
                .build();
        tour.getImages().stream().forEach(t -> t.setTour(tour));
        return tour;
    }

    private TourResponseDTO toDto(Tour t) {
        TourResponseDTO dto = new TourResponseDTO();
        dto.setId(t.getId());
        dto.setTitle(t.getTitle());
        dto.setDescription(t.getDescription());
        dto.setCapacity(t.getCapacity());
        dto.setPriceAdults(t.getPriceAdults());
        dto.setPriceChildren(t.getPriceChildren());
        dto.setStartDate(t.getStartDate());
        dto.setEndDate(t.getEndDate());
        dto.setDestination(t.getDestination());
        dto.setRegion(t.getRegion());
        dto.setCategory(t.getCategory());
        dto.setAirline(t.getAirline());
        dto.setCode(t.getCode());
        dto.setAvailable(t.isAvailable());
        dto.setItinerary(t.getItinerary());
        dto.setDurationDays(t.getDurationDays());
        dto.setDurationNights(t.getDurationNights());
        dto.setImages(
                t.getImages().stream()
                        .map(ImageDTO::fromDomain)
                        .collect(Collectors.toList())
        );
        return dto;
    }

    private void validateTourRequest(TourRequestDTO dto) {
        if (dto.getTitle() == null || dto.getTitle().isBlank()) {
            throw new InvalidDataException("Title không được để trống");
        }

        if (dto.getDescription() != null && dto.getDescription().length() > 2000) {
            throw new InvalidDataException("Description không được dài quá 2000 ký tự");
        }

        if (dto.getCapacity() == null || dto.getCapacity() < 1) {
            throw new InvalidDataException("Capacity phải lớn hơn 0");
        }

        if (dto.getPriceAdults() == null || dto.getPriceAdults() <= 0) {
            throw new InvalidDataException("Giá người lớn phải > 0");
        }

        if (dto.getPriceChildren() == null || dto.getPriceChildren() < 0) {
            throw new InvalidDataException("Giá trẻ em phải >= 0");
        }

        if (dto.getStartDate() == null) {
            throw new InvalidDataException("Ngày bắt đầu không được để trống");
        }

        if (dto.getEndDate() == null) {
            throw new InvalidDataException("Ngày kết thúc không được để trống");
        }

        if (dto.getStartDate().isAfter(dto.getEndDate())) {
            throw new InvalidDataException("Ngày bắt đầu phải trước ngày kết thúc");
        }

        if (dto.getDestination() == null || dto.getDestination().isBlank()) {
            throw new InvalidDataException("Destination không được để trống");
        }

        if (dto.getRegion() == null) {
            throw new InvalidDataException("Region không được để trống");
        }

        if (dto.getCategory() == null) {
            throw new InvalidDataException("Category không được để trống");
        }

        if (dto.getCode() == null || dto.getCode().isBlank()) {
            throw new InvalidDataException("Code tour không được để trống");
        }
    }


    @Override
    public Page<TourResponseDTO> getAllTours(int page, int size, Boolean isDeleted) {
        Page<Tour> tourPage = isDeleted != null ? tourRepository.findAllByDeletedFalse((Pageable) PageRequest.of(page, size))
                : tourRepository.findAll((Pageable) PageRequest.of(page, size));
        return tourPage.map(this::toDto);
    }

    @Override
    public byte[] exportToursToPdf(int page, int size) {
        return exportToursToPdf(getAllTours(page, size, null).getContent());
    }

    @Override
    public ByteArrayInputStream exportToursToExcel(int page, int size) throws IOException {
        return toursToExcel(getAllTours(page, size, null).getContent());
    }

    @Override
    public List<TourResponseDTO> searchTours(TourSearchRequest request, Boolean isDeleted) {
        Specification<Tour> spec = TourSpecification.search(request, isDeleted);
        return tourRepository.findAll(spec).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }


    public byte[] exportToursToPdf(List<TourResponseDTO> tours) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf, PageSize.A4.rotate());

            // Font tùy chỉnh hỗ trợ tiếng Việt
            PdfFont font;
            PdfFont fontBold;
            try {
                String regularFontPath = getClass().getClassLoader().getResource("fonts/NotoSans-Regular.ttf").getPath();
                String boldFontPath = getClass().getClassLoader().getResource("fonts/NotoSans-Bold.ttf").getPath();

                font = PdfFontFactory.createFont(regularFontPath, PdfEncodings.IDENTITY_H);
                fontBold = PdfFontFactory.createFont(boldFontPath, PdfEncodings.IDENTITY_H);

            } catch (IOException e) {
                throw new RuntimeException("Không thể load font tùy chỉnh", e);
            }

            document.setFont(font);
            document.setFontSize(10);

            Paragraph title = new Paragraph("DANH SÁCH CÁC TOUR DU LỊCH")
                    .setFont(fontBold)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(14)
                    .setBold()
                    .setMarginBottom(15);
            document.add(title);

            float[] columnWidths = {4, 8, 2, 3, 3, 3, 3, 3, 4, 5, 10};
            Table table = new Table(UnitValue.createPercentArray(columnWidths)).useAllAvailableWidth();

            String[] headers = {
                    "Tiêu đề", "Mô tả", "Sức chứa", "Giá Người Lớn", "Giá Trẻ Em", "Mã",
                    "Thể loại", "Khu vực", "Điểm đến", "Ngày", "Lịch trình"
            };

            for (String h : headers) {
                table.addHeaderCell(new com.itextpdf.layout.element.Cell()
                        .add(new Paragraph(h).setFont(fontBold))
                        .setBackgroundColor(ColorConstants.LIGHT_GRAY));
            }

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            for (TourResponseDTO tour : tours) {
                table.addCell(new Paragraph(tour.getTitle()));
                table.addCell(new Paragraph(tour.getDescription()));
                table.addCell(new Paragraph(String.valueOf(tour.getCapacity())));
                table.addCell(new Paragraph(String.format("%,.0f", tour.getPriceAdults())));
                table.addCell(new Paragraph(String.format("%,.0f", tour.getPriceChildren())));
                table.addCell(new Paragraph(tour.getCode()));
                table.addCell(new Paragraph(tour.getCategory().toString()));
                table.addCell(new Paragraph(tour.getRegion().toString()));
                table.addCell(new Paragraph(tour.getDestination()));
                table.addCell(new Paragraph(
                        tour.getStartDate().format(formatter) + " - " +
                                tour.getEndDate().format(formatter)
                ));
                table.addCell(new Paragraph(String.join("\n", tour.getItinerary())));
            }

            document.add(table);
            document.close();
            return baos.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error generating PDF", e);
        }
    }

    public static ByteArrayInputStream toursToExcel(List<TourResponseDTO> tours) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Tours");

            // Header row
            Row headerRow = sheet.createRow(0);
            String[] headers = {"ID", "Title", "Description", "Capacity", "Price Adults", "Price Children",
                    "Start Date", "End Date", "Destination", "Region", "Category", "Airline", "Code", "Available", "Itinerary"};

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                CellStyle style = workbook.createCellStyle();
                Font font = workbook.createFont();
                font.setBold(true);
                style.setFont(font);
                cell.setCellStyle(style);
            }

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

            // Data rows
            int rowIdx = 1;
            for (TourResponseDTO tour : tours) {
                Row row = sheet.createRow(rowIdx++);

                row.createCell(0).setCellValue(tour.getId());
                row.createCell(1).setCellValue(tour.getTitle());
                row.createCell(2).setCellValue(tour.getDescription());
                row.createCell(3).setCellValue(tour.getCapacity());
                row.createCell(4).setCellValue(tour.getPriceAdults());
                row.createCell(5).setCellValue(tour.getPriceChildren());
                row.createCell(6).setCellValue(tour.getStartDate() != null ? tour.getStartDate().format(formatter) : "");
                row.createCell(7).setCellValue(tour.getEndDate() != null ? tour.getEndDate().format(formatter) : "");
                row.createCell(8).setCellValue(tour.getDestination());
                row.createCell(9).setCellValue(tour.getRegion() != null ? tour.getRegion().name() : "");
                row.createCell(10).setCellValue(tour.getCategory() != null ? tour.getCategory().name() : "");
                row.createCell(11).setCellValue(tour.getAirline());
                row.createCell(12).setCellValue(tour.getCode());
                row.createCell(13).setCellValue(tour.getAvailable());
                row.createCell(14).setCellValue(String.join(", ", tour.getItinerary() != null ? tour.getItinerary() : List.of()));
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }

    public Tour updateAvailability(Long tourId, boolean available) {
        Tour tour = tourRepository.findById(tourId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy tour với ID: " + tourId));

        tour.setAvailable(available);
        return tourRepository.save(tour);
    }
}
