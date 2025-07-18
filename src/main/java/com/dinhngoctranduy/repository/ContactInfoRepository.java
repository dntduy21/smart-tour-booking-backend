package com.dinhngoctranduy.repository;

import com.dinhngoctranduy.model.ContactInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ContactInfoRepository extends JpaRepository<ContactInfo, Long> {
    Optional<ContactInfo> findByIsPrimary(boolean isPrimary);
}
