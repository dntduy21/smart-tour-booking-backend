package com.dinhngoctranduy.model.dto.statistic;

import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class RevenueByDate {
    private Object date;
    private Object revenue;
}