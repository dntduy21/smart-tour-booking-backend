package com.dinhngoctranduy.service.impl;

import com.dinhngoctranduy.model.CustomTour;
import com.dinhngoctranduy.model.request.CustomTourRequest;
import com.dinhngoctranduy.model.response.CustomTourResponse;
import com.dinhngoctranduy.repository.CustomTourRepository;
import com.dinhngoctranduy.service.CustomTourService;
import com.dinhngoctranduy.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomTourServiceImpl implements CustomTourService {

    private final CustomTourRepository customTourRepository;
    private final EmailService emailService;

    public CustomTourResponse createCustomTour(CustomTourRequest request) {
        CustomTour tour = CustomTour.builder()
                .name(request.getName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .description(request.getDescription())
                .capacity(request.getCapacity())
                .destination(request.getDestination())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .region(request.getRegion())
                .adultsCapacity(request.getAdultsCapacity())
                .childrenCapacity(request.getChildrenCapacity())
                .status(false)
                .deleted(false)
                .build();

        CustomTour saved = customTourRepository.save(tour);

        return mapToResponse(saved);
    }

    public List<CustomTourResponse> getAllCustomTours(Pageable pageable) {
        return customTourRepository.findByDeletedFalse(pageable)
                .getContent()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public CustomTourResponse getCustomTourById(Long id, Boolean isDeleted) {
        CustomTour tour = (isDeleted != null ? customTourRepository.findByIdAndDeletedFalse(id) : customTourRepository.findById(id))
                .orElseThrow(() -> new RuntimeException("Custom tour not found with id: " + id));
        return mapToResponse(tour);
    }

    public void sendReplyToCustomer(Long tourId, String message) {
        CustomTour tour = customTourRepository.findByIdAndDeletedFalse(tourId)
                .orElseThrow(() -> new RuntimeException("Custom tour not found with id: " + tourId));

        String subject = "Phản hồi từ SmartTour về yêu cầu đặt tour của bạn";

        String htmlContent = buildCustomTourReplyHtml(tour.getName(), message);

        CompletableFuture.runAsync(() -> emailService.sendHtmlEmail(tour.getEmail(), subject, htmlContent));
    }

    private String buildCustomTourReplyHtml(String customerName, String message) {
        return """
                <html>
                <head>
                    <meta charset="UTF-8">
                    <style>
                        body { font-family: Arial, sans-serif; }
                        .header { background-color: #f8f8f8; padding: 12px; }
                        .content { margin-top: 20px; padding: 10px; }
                        .footer { margin-top: 30px; font-size: 13px; color: #888; }
                    </style>
                </head>
                <body>
                    <div class="header">
                        <h2>Xin chào %s,</h2>
                    </div>
                    <div class="content">
                        <p>%s</p>
                    </div>
                    <div class="footer">
                        <p>Trân trọng,</p>
                        <p><b>Đội ngũ hỗ trợ SmartTour</b></p>
                    </div>
                </body>
                </html>
                """.formatted(customerName, message.replace("\n", "<br>"));
    }


    private CustomTourResponse mapToResponse(CustomTour tour) {
        return CustomTourResponse.builder()
                .id(tour.getId())
                .name(tour.getName())
                .email(tour.getEmail())
                .phone(tour.getPhone())
                .description(tour.getDescription())
                .destination(tour.getDestination())
                .startDate(tour.getStartDate())
                .endDate(tour.getEndDate())
                .capacity(tour.getCapacity())
                .adultsCapacity(tour.getAdultsCapacity())
                .childrenCapacity(tour.getChildrenCapacity())
                .region(tour.getRegion())
                .durationDays(tour.getDurationDays())
                .durationNights(tour.getDurationNights())
                .status(tour.isStatus())
                .build();
    }

    public void softDeleteCustomTour(Long id) {
        CustomTour tour = customTourRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Custom Tour not found with id: " + id));

        tour.setDeleted(true);
        customTourRepository.save(tour);
    }

    public CustomTourResponse updateCustomTourStatus(Long id, boolean newStatus) {
        CustomTour tour = customTourRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Custom Tour not found with id: " + id));

        tour.setStatus(newStatus);
        CustomTour updatedTour = customTourRepository.save(tour);
        return mapToResponse(updatedTour);
    }
}
