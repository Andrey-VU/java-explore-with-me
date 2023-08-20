package ru.practicum.event.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.user.mapper.UserMapper;
import ru.practicum.user.model.User;
import ru.practicum.user.model.UserService;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@Component
@AllArgsConstructor
public class EventMapperService {
    private final UserService userService;
    private final UserMapper userMapper;
    private final ViewService viewService;

    User makeUser(Long userId) {
        return userMapper.makeUserFromDto(userService.get(userId));
    }

    Long addViews(Long eventId, HttpServletRequest request) {
        Long views = viewService.getViewsById(eventId, request);
        log.info("Событие Id {} было просмотрено {} раз", eventId, views);
        return views;
    }
}


