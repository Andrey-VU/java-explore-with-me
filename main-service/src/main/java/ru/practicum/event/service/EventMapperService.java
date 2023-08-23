package ru.practicum.event.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.category.model.Category;
import ru.practicum.category.model.CategoryService;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.enums.EventState;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.Location;
import ru.practicum.exception.EwmConflictException;
import ru.practicum.request.service.RequestService;
import ru.practicum.user.mapper.UserMapper;
import ru.practicum.user.model.User;
import ru.practicum.user.model.UserService;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Slf4j
@Component
@AllArgsConstructor
public class EventMapperService {
    private final UserService userService;
    private final ViewService viewService;
    private final CategoryService categoryService;
    private final LocationService locationService;
    private final CategoryMapper categoryMapper;
    private final EventMapper eventMapper;
    private final UserMapper userMapper;
    private final RequestService requestService;

    User makeUser(Long userId) {
        return userMapper.makeUserFromDto(userService.get(userId));
    }

    Long addViews(Long eventId) {
        Long views = viewService.getViewsById(eventId);
        log.info("Событие Id {} было просмотрено {} раз", eventId, views);
        return views;
    }

    public Long getParticipants(Long eventId) {
        return requestService.getConfirmedRequestsById(eventId);
    }

    public void saveStatistics(HttpServletRequest request) {
        viewService.saveHit(request.getRequestURI(), request.getLocalAddr());
    }

    public Event prepareForUpdate(Event eventFromRepo, EventFullDto updateForEvent) {
        if (eventMapper.makeFullDto(eventFromRepo).equals(updateForEvent)){
            log.info("Пакет обновлений идентичен содержанию сохранённого События. Обновление не требуется");
            throw new EwmConflictException("Обновления не выполнено: входящий пакет не содержит новой информации");
        }
        if (updateForEvent.getState().equals(EventState.PUBLISHED)) {
            log.warn("Событие не может быть обновлено. Конфликт состояния");
            throw new EwmConflictException("изменить можно только отмененные события или события в состоянии " +
                "ожидания модерации");
        }
        if (updateForEvent.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            log.warn("Событие не может быть обновлено. Конфликт дат");
            throw new EwmConflictException("Дата и время на которые намечено событие не может быть раньше, чем " +
                "через два часа от текущего момента");
        }
        Event newEvent =  eventFromRepo;
        if (updateForEvent.getPaid() != null && !newEvent.getPaid().equals(updateForEvent.getPaid())) {
            newEvent.setPaid(updateForEvent.getPaid());
        }
        if (updateForEvent.getEventDate() != null && !newEvent.getEventDate().equals(updateForEvent.getEventDate())){
            newEvent.setEventDate(updateForEvent.getEventDate());
        }
        if (updateForEvent.getAnnotation() != null && !newEvent.getAnnotation().equals(updateForEvent.getAnnotation())){
            newEvent.setAnnotation(updateForEvent.getAnnotation());
        }
        if (updateForEvent.getCategory().getId() != null
            && newEvent.getCategory().getId() != (updateForEvent.getCategory().getId())){
            newEvent.setCategory(categoryMapper.makeCategoryFromCategoryDto(updateForEvent.getCategory()));
        }
        if (updateForEvent.getDescription() != null
            && !newEvent.getDescription().equals(updateForEvent.getDescription())){
            newEvent.setDescription(updateForEvent.getDescription());
        }
        if (updateForEvent.getTitle() != null && !newEvent.getTitle().equals(updateForEvent.getTitle())){
            newEvent.setTitle(updateForEvent.getTitle());
        }
        if (updateForEvent.getLocation() != null
            && newEvent.getLocation().getId() != updateForEvent.getLocation().getId()){
            newEvent.setLocation(updateForEvent.getLocation());
        }
        if (updateForEvent.getParticipantLimit() != null
            && newEvent.getParticipantLimit() != updateForEvent.getParticipantLimit()){
            newEvent.setParticipantLimit(updateForEvent.getParticipantLimit());
        }
        if (updateForEvent.getRequestModeration() != null
            && newEvent.getRequestModeration() != updateForEvent.getRequestModeration()){
            newEvent.setRequestModeration(updateForEvent.getRequestModeration());
        }
        log.info("Event id {} ready for update", eventFromRepo.getId() );
        return newEvent;
    }

    public Category makeCategory(Long category) {
        return categoryMapper.makeCategoryFromCategoryDto(categoryService.getPublic(category));
    }

    public Location saveLocation(NewEventDto dto) {
        return locationService.save(dto.getLocation());
    }

}


