package com.dinhngoctranduy.controller;

import com.dinhngoctranduy.model.dto.ContactInfoDTO;
import com.dinhngoctranduy.service.ContactInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1") // Base path
public class ContactInfoController {

    @Autowired
    private ContactInfoService contactInfoService;

    // ================== ADMIN ENDPOINTS ==================

    @GetMapping("/contacts")
    public ResponseEntity<List<ContactInfoDTO>> getAllContacts() {
        return ResponseEntity.ok(contactInfoService.findAll());
    }

    @GetMapping("/contacts/{id}")
    public ResponseEntity<ContactInfoDTO> getContactById(@PathVariable Long id) {
        return ResponseEntity.ok(contactInfoService.findById(id));
    }

    @PostMapping("/contacts")
    public ResponseEntity<ContactInfoDTO> createContact(@RequestBody ContactInfoDTO contactInfoDTO) {
        ContactInfoDTO savedDto = contactInfoService.save(contactInfoDTO);
        return new ResponseEntity<>(savedDto, HttpStatus.CREATED);
    }

    @PutMapping("/contacts/{id}")
    public ResponseEntity<ContactInfoDTO> updateContact(@PathVariable Long id, @RequestBody ContactInfoDTO contactInfoDTO) {
        return ResponseEntity.ok(contactInfoService.update(id, contactInfoDTO));
    }

    @DeleteMapping("/contacts/{id}")
    public ResponseEntity<Void> deleteContact(@PathVariable Long id) {
        contactInfoService.deleteById(id);
        return ResponseEntity.noContent().build();
    }


    // ================== PUBLIC ENDPOINT ==================

    @GetMapping("/public/contact/primary")
    public ResponseEntity<ContactInfoDTO> getPrimaryContact() {
        return ResponseEntity.ok(contactInfoService.findPrimary());
    }
}
