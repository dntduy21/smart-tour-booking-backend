package com.dinhngoctranduy.model.dto.statistic;

import lombok.*;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RevenueByYear {
    private Object year;
    private Object revenue;
}