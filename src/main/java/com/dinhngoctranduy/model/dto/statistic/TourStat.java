package com.dinhngoctranduy.model.dto.statistic;

import lombok.*;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TourStat {
    private Object tourId;
    private Object tourTitle;
    private Object count; // lượt đặt hoặc lượt huỷ
}