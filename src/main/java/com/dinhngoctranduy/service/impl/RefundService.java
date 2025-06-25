package com.dinhngoctranduy.service.impl;

import com.dinhngoctranduy.util.error.InvalidDataException;
import com.dinhngoctranduy.model.Refund;
import com.dinhngoctranduy.model.dto.RefundDTO;
import com.dinhngoctranduy.repository.RefundRepository;
import com.dinhngoctranduy.util.constant.RefundStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class RefundService {

    private final RefundRepository refundRepository;

    public RefundDTO updateRefundStatusByBookingId(Long bookingId, RefundStatus status) {
        Refund refund = refundRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new InvalidDataException("Refund not found for bookingId: " + bookingId));

        if(refund.getStatus() == RefundStatus.DONE) {
            throw new InvalidDataException("Refund is already DONE");
        }

        refund.setStatus(status);

        if (status == RefundStatus.DONE) {
            refund.setRefundAt(Instant.now());
        }

        Refund r = refundRepository.save(refund);
        return RefundDTO.mapToDto(r);
    }

}
