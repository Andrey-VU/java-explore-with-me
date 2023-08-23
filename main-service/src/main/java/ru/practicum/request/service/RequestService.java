package ru.practicum.request.service;

import ru.practicum.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.request.dto.ParticipationRequestDto;

import java.util.List;

public interface RequestService {
    List<ParticipationRequestDto> getRequestsListPrivate(Long userId, Long eventId);
    EventRequestStatusUpdateResult updateResultRequestsListPrivate(Long userId, Long eventId,
                                                                   EventRequestStatusUpdateRequest requestDto);
    ParticipationRequestDto cancelPrivate(Long userId, Long requestId);
    ParticipationRequestDto createPrivate(Long userId, Long requestDto);
    List<ParticipationRequestDto> getPrivate(Long userId);
    Long getConfirmedRequestsById(Long eventId);
}
