package com.dinhngoctranduy.service.impl;

import com.dinhngoctranduy.service.FileStorageService;
import com.dinhngoctranduy.util.error.InvalidDataException;
import com.dinhngoctranduy.model.Refund;
import com.dinhngoctranduy.model.dto.RefundDTO;
import com.dinhngoctranduy.repository.RefundRepository;
import com.dinhngoctranduy.util.constant.RefundStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class RefundService {

    private final RefundRepository refundRepository;
    private final FileStorageService fileStorageService;

    public RefundDTO updateRefundStatusAndProof(Long bookingId, RefundStatus status, MultipartFile proofImage) {
        Refund refund = refundRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new InvalidDataException("Không tìm thấy thông tin hoàn tiền cho đơn hàng: " + bookingId));

        if (refund.getStatus() == RefundStatus.DONE) {
            throw new InvalidDataException("Yêu cầu hoàn tiền này đã được hoàn tất.");
        }

        // Chỉ xử lý file khi trạng thái là DONE
        if (status == RefundStatus.DONE) {
            if (proofImage == null || proofImage.isEmpty()) {
                throw new InvalidDataException("Cần có ảnh minh chứng để cập nhật trạng thái là DONE.");
            }

            // 1. Gọi service của bạn để lưu file và lấy URL
            String imageUrl = fileStorageService.storeFile(proofImage);

            // 2. Cập nhật thông tin vào entity
            refund.setProofImageUrl(imageUrl);
            refund.setRefundAt(Instant.now());
        }

        refund.setStatus(status);
        Refund savedRefund = refundRepository.save(refund);

        return RefundDTO.mapToDto(savedRefund); // Giả sử bạn có hàm map này
    }

}
