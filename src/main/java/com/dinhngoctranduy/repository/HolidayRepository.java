package com.dinhngoctranduy.repository;

import com.dinhngoctranduy.model.Holiday;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HolidayRepository extends JpaRepository<Holiday, Long> {
    List<Holiday> findAll();
}
