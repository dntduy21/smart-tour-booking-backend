package com.dinhngoctranduy.service;

import com.dinhngoctranduy.model.Participant;

import java.util.List;

public interface ParticipantService {
    Participant addParticipantToBooking(Long bookingId, Participant newParticipant);

    Participant updateParticipant(Long participantId, Participant updatedInfo);

    void deleteParticipant(Long participantId);

    Participant getParticipantById(Long participantId);

    List<Participant> getParticipantsByBookingId(Long bookingId);
}