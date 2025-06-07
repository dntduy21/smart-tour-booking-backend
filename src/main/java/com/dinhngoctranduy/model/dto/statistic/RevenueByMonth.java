package com.dinhngoctranduy.model.dto.statistic;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import lombok.*;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RevenueByMonth {
    private Object year;
    private Object month;
    private Object revenue;
}