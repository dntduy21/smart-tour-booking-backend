package com.dinhngoctranduy.controller;

import com.dinhngoctranduy.model.Holiday;
import com.dinhngoctranduy.repository.HolidayRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/holidays")
public class HolidayController {
    @Autowired
    private HolidayRepository holidayRepository;

    @GetMapping
    public List<Holiday> getAll() {
        return holidayRepository.findAll();
    }

    @PostMapping
    public Holiday add(@RequestBody Holiday holiday) {
        return holidayRepository.save(holiday);
    }

    @PutMapping("/{id}")
    public Holiday update(@PathVariable Long id, @RequestBody Holiday holiday) {
        Holiday existing = holidayRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Holiday not found"));
        existing.setMonth(holiday.getMonth());
        existing.setDay(holiday.getDay());
        existing.setDescription(holiday.getDescription());
        return holidayRepository.save(existing);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        holidayRepository.deleteById(id);
    }
}
