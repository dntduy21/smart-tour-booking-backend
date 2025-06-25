package com.dinhngoctranduy.model.request;

import com.dinhngoctranduy.util.constant.RefundStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateRefundStatusRequest {
    private RefundStatus status;
}
