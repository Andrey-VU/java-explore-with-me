package ru.practicum.event.service;

import org.springframework.stereotype.Service;
import ru.practicum.category.model.Category;
import ru.practicum.event.dto.*;
import ru.practicum.event.enums.EventState;
import ru.practicum.event.enums.SortBy;
import ru.practicum.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.user.model.User;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

@Service
public interface EventService {
    List<EventFullDto> getAdmin(List<User> users, List<EventState> states, List<Category> categories,
                                LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from,
                                Integer size);

    EventFullDto updateAdmin(Long eventId, UpdateEventAdminRequest updateRequestDto);

    List<EventFullDto> getPublicEvents(String text, Boolean paid, List<Category> categories, LocalDateTime rangeStart,
                                       LocalDateTime rangeEnd, Boolean onlyAvailable, SortBy sort, Integer from,
                                       Integer size, HttpServletRequest request);
    EventFullDto getPublic(Long id, HttpServletRequest request);

    List<EventShortDto> getListPrivate(Long userId,Integer from, Integer size);

    EventFullDto create(Long userId, NewEventDto dto);

    EventFullDto getFullDtoEventPrivate(Long userId, Long eventId);

    EventFullDto updatePrivate(Long userId, Long eventId, UpdateEventUserRequest updateForEvent);
}
