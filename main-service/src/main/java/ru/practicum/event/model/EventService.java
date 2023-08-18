package ru.practicum.event.model;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import ru.practicum.category.model.Category;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.UpdateEventAdminRequest;
import ru.practicum.event.enums.EventState;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

@Service
public interface EventService {
    List<EventFullDto> getAdmin(List<User> users, List<EventState> states, List<Category> categories,
                                LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from,
                                Integer size);

    EventFullDto updateAdmin(Long eventId, UpdateEventAdminRequest updateRequestDto);




}
