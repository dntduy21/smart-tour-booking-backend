package com.dinhngoctranduy.service.impl;

import com.dinhngoctranduy.model.ContactInfo;
import com.dinhngoctranduy.model.dto.ContactInfoDTO;
import com.dinhngoctranduy.repository.ContactInfoRepository;
import com.dinhngoctranduy.service.ContactInfoService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ContactInfoServiceImpl implements ContactInfoService {

    @Autowired
    private ContactInfoRepository contactInfoRepository;

    @Override
    public List<ContactInfoDTO> findAll() {
        return contactInfoRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ContactInfoDTO findById(Long id) {
        ContactInfo contactInfo = contactInfoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Contact not found with id: " + id));
        return convertToDTO(contactInfo);
    }

    @Override
    public ContactInfoDTO save(ContactInfoDTO contactInfoDTO) {
        // Nếu liên hệ mới được set là primary, bỏ primary của liên hệ cũ
        if (contactInfoDTO.getIsPrimary()) {
            unsetCurrentPrimary();
        }
        ContactInfo contactInfo = convertToEntity(contactInfoDTO);
        ContactInfo savedContact = contactInfoRepository.save(contactInfo);
        return convertToDTO(savedContact);
    }

    @Override
    public ContactInfoDTO update(Long id, ContactInfoDTO contactInfoDTO) {
        // Lấy entity đang tồn tại từ DB
        ContactInfo existingContact = contactInfoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Contact not found with id: " + id));

        // 💡 Logic kiểm tra và cập nhật từng trường nếu nó được cung cấp (không null)
        if (contactInfoDTO.getName() != null) {
            existingContact.setName(contactInfoDTO.getName());
        }
        if (contactInfoDTO.getAddress() != null) {
            existingContact.setAddress(contactInfoDTO.getAddress());
        }
        if (contactInfoDTO.getPhone() != null) {
            existingContact.setPhone(contactInfoDTO.getPhone());
        }
        if (contactInfoDTO.getEmail() != null) {
            existingContact.setEmail(contactInfoDTO.getEmail());
        }
        if (contactInfoDTO.getWorkingHours() != null) {
            existingContact.setWorkingHours(contactInfoDTO.getWorkingHours());
        }

        // Xử lý logic cho trường `isPrimary`
        if (contactInfoDTO.getIsPrimary() != null) {
            // Nếu được set là true và nó chưa phải là primary
            if (contactInfoDTO.getIsPrimary() && !existingContact.isPrimary()) {
                unsetCurrentPrimary();
            }
            existingContact.setPrimary(contactInfoDTO.getIsPrimary());
        }

        ContactInfo updatedContact = contactInfoRepository.save(existingContact);
        return convertToDTO(updatedContact);
    }

    @Override
    public void deleteById(Long id) {
        if (!contactInfoRepository.existsById(id)) {
            throw new EntityNotFoundException("Contact not found with id: " + id);
        }
        contactInfoRepository.deleteById(id);
    }

    @Override
    public ContactInfoDTO findPrimary() {
        return contactInfoRepository.findByIsPrimary(true)
                .map(this::convertToDTO)
                .orElseThrow(() -> new EntityNotFoundException("No primary contact configured."));
    }

    // --- Helper Methods ---

    private void unsetCurrentPrimary() {
        contactInfoRepository.findByIsPrimary(true).ifPresent(oldPrimary -> {
            oldPrimary.setPrimary(false);
            contactInfoRepository.save(oldPrimary);
        });
    }

    private ContactInfoDTO convertToDTO(ContactInfo contactInfo) {
        return new ContactInfoDTO(
                contactInfo.getId(),
                contactInfo.getName(),
                contactInfo.getAddress(),
                contactInfo.getPhone(),
                contactInfo.getEmail(),
                contactInfo.getWorkingHours(),
                contactInfo.isPrimary()
        );
    }

    private ContactInfo convertToEntity(ContactInfoDTO dto) {
        return ContactInfo.builder()
                .id(dto.getId())
                .name(dto.getName())
                .address(dto.getAddress())
                .phone(dto.getPhone())
                .email(dto.getEmail())
                .workingHours(dto.getWorkingHours())
                .isPrimary(dto.getIsPrimary())
                .build();
    }
}