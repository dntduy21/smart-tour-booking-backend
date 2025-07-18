package com.dinhngoctranduy.service;

import com.dinhngoctranduy.model.dto.ContactInfoDTO;

import java.util.List;

public interface ContactInfoService {
    List<ContactInfoDTO> findAll();

    ContactInfoDTO findById(Long id);

    ContactInfoDTO save(ContactInfoDTO contactInfoDTO);

    ContactInfoDTO update(Long id, ContactInfoDTO contactInfoDTO);

    void deleteById(Long id);

    ContactInfoDTO findPrimary();
}
