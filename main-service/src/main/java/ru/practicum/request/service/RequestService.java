package ru.practicum.request.service;

import ru.practicum.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.request.dto.ParticipationRequestDto;

import java.util.List;

public interface RequestService {
    List<ParticipationRequestDto> getRequestsListPrivate(Long userId, Long eventId);
    EventRequestStatusUpdateResult getResultRequestsListPrivate(Long userId, Long eventId);
}
