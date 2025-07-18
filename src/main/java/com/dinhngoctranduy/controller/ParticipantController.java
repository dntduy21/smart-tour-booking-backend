package com.dinhngoctranduy.controller;

import com.dinhngoctranduy.model.Participant;
import com.dinhngoctranduy.service.ParticipantService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/participants")
@AllArgsConstructor
public class ParticipantController {

    private final ParticipantService participantService;

    // Lấy danh sách người tham gia theo ID của booking
    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<List<Participant>> getParticipantsByBooking(@PathVariable Long bookingId) {
        return ResponseEntity.ok(participantService.getParticipantsByBookingId(bookingId));
    }

    // Thêm một người tham gia mới vào booking
    @PostMapping("/booking/{bookingId}")
    public ResponseEntity<Participant> addParticipant(@PathVariable Long bookingId, @RequestBody Participant participant) {
        Participant savedParticipant = participantService.addParticipantToBooking(bookingId, participant);
        return new ResponseEntity<>(savedParticipant, HttpStatus.CREATED);
    }

    // Lấy thông tin một người tham gia
    @GetMapping("/{participantId}")
    public ResponseEntity<Participant> getParticipant(@PathVariable Long participantId) {
        return ResponseEntity.ok(participantService.getParticipantById(participantId));
    }

    // Cập nhật thông tin của một người tham gia
    @PutMapping("/{participantId}")
    public ResponseEntity<Participant> updateParticipant(@PathVariable Long participantId, @RequestBody Participant participant) {
        return ResponseEntity.ok(participantService.updateParticipant(participantId, participant));
    }

    // Xóa một người tham gia
    @DeleteMapping("/{participantId}")
    public ResponseEntity<Void> deleteParticipant(@PathVariable Long participantId) {
        participantService.deleteParticipant(participantId);
        return ResponseEntity.noContent().build();
    }
}
