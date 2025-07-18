package com.dinhngoctranduy.service.impl;

import com.dinhngoctranduy.model.Booking;
import com.dinhngoctranduy.model.Participant;
import com.dinhngoctranduy.repository.BookingRepository;
import com.dinhngoctranduy.repository.ParticipantRepository;
import com.dinhngoctranduy.service.ParticipantService;
import com.dinhngoctranduy.util.error.InvalidDataException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
public class ParticipantServiceImpl implements ParticipantService {

    private final ParticipantRepository participantRepo;
    private final BookingRepository bookingRepo;

    @Override
    @Transactional
    public Participant addParticipantToBooking(Long bookingId, Participant newParticipant) {
        Booking booking = bookingRepo.findById(bookingId)
                .orElseThrow(() -> new InvalidDataException("Booking not found with id: " + bookingId));

        // Kiểm tra xem type có hợp lệ không
        if (!Objects.equals(newParticipant.getType(), "ADULT") && !Objects.equals(newParticipant.getType(), "CHILD")) {
            throw new InvalidDataException("Participant type must be 'ADULT' or 'CHILD'");
        }

        // Cập nhật lại số lượng trong booking
        if ("ADULT".equals(newParticipant.getType())) {
            booking.setAdults(booking.getAdults() + 1);
        } else {
            booking.setChildren(booking.getChildren() + 1);
        }

        // Liên kết và lưu participant
        newParticipant.setBooking(booking);
        return participantRepo.save(newParticipant);
    }

    @Override
    @Transactional
    public Participant updateParticipant(Long participantId, Participant updatedInfo) {
        Participant existingParticipant = participantRepo.findById(participantId)
                .orElseThrow(() -> new InvalidDataException("Participant not found with id: " + participantId));

        Booking booking = existingParticipant.getBooking();
        if (booking == null) {
            throw new InvalidDataException("Participant must be associated with a booking");
        }

        String oldType = existingParticipant.getType();   // Giữ type cũ để so sánh
        String newType = updatedInfo.getType();

        // Nếu type thay đổi, cập nhật lại số lượng adults/children trong booking
        if (!Objects.equals(oldType, newType)) {
            if ("ADULT".equals(oldType)) {
                booking.setAdults(Math.max(booking.getAdults() - 1, 0));
            } else if ("CHILD".equals(oldType)) {
                booking.setChildren(Math.max(booking.getChildren() - 1, 0));
            }

            if ("ADULT".equals(newType)) {
                booking.setAdults(booking.getAdults() + 1);
            } else if ("CHILD".equals(newType)) {
                booking.setChildren(booking.getChildren() + 1);
            } else {
                throw new InvalidDataException("Participant type must be 'ADULT' or 'CHILD'");
            }
        }

        // Cập nhật thông tin
        existingParticipant.setName(updatedInfo.getName());
        existingParticipant.setPhone(updatedInfo.getPhone());
        existingParticipant.setGender(updatedInfo.getGender());
        existingParticipant.setType(newType); // Cập nhật lại type nếu đã đổi

        return participantRepo.save(existingParticipant);
    }

    @Override
    @Transactional
    public void deleteParticipant(Long participantId) {
        // Tìm participant và booking liên quan
        Participant participant = participantRepo.findById(participantId)
                .orElseThrow(() -> new InvalidDataException("Participant not found with id: " + participantId));

        Booking booking = participant.getBooking();

        // Cập nhật lại số lượng trong booking trước khi xóa
        if ("ADULT".equals(participant.getType())) {
            // Đảm bảo số lượng không bị âm
            if (booking.getAdults() > 0) {
                booking.setAdults(booking.getAdults() - 1);
            }
        } else {
            if (booking.getChildren() > 0) {
                booking.setChildren(booking.getChildren() - 1);
            }
        }

        // Xóa participant khỏi database
        participantRepo.delete(participant);
    }

    @Override
    public Participant getParticipantById(Long participantId) {
        return participantRepo.findById(participantId)
                .orElseThrow(() -> new InvalidDataException("Participant not found with id: " + participantId));
    }

    @Override
    public List<Participant> getParticipantsByBookingId(Long bookingId) {
        if (!bookingRepo.existsById(bookingId)) {
            throw new InvalidDataException("Booking not found with id: " + bookingId);
        }
        return participantRepo.findByBookingId(bookingId);
    }
}
