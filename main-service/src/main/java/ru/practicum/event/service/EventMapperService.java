package ru.practicum.event.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.category.model.Category;
import ru.practicum.category.model.CategoryService;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.dto.UpdateEventAdminRequest;
import ru.practicum.event.enums.EventState;
import ru.practicum.event.enums.StateActionAdmin;
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
        return updateFieldsWithoutState(eventFromRepo, updateForEvent);
    }

    public Category makeCategory(Long category) {
        return categoryMapper.makeCategoryFromCategoryDto(categoryService.getPublic(category));
    }

    public Event prepareForAdminUpdate(Event eventFromRepo, UpdateEventAdminRequest updateRequestDto) {
        if (eventMapper.makeFullDto(eventFromRepo).equals(eventMapper.makeUpdateAdmin(updateRequestDto))) {
            log.info("Пакет обновлений идентичен содержанию сохранённого События. Обновление не требуется");
            throw new EwmConflictException("Обновления не выполнено: входящий пакет не содержит новой информации");
        }
        if (!updateRequestDto.getEventDate().minusHours(1).isBefore(eventFromRepo.getPublishedOn())) {
            log.warn("Событие не может быть обновлено. Конфликт дат");
            throw new EwmConflictException("Изменяемое событие должно начинаться позже, чем через час после публикации!");
        }
        if (!eventFromRepo.getState().equals(EventState.PENDING)) {
            log.warn("Событие не может быть опубликовано. Конфликт состояния");
            throw new EwmConflictException("Опубликовать можно события в статусе ожидания публикации");
        }
        if (updateRequestDto.getStateAction().equals(StateActionAdmin.REJECT_EVENT)
            && eventFromRepo.getState().equals(EventState.PUBLISHED)
            || eventFromRepo.getState().equals(EventState.CANCELED)) {
            log.warn("Cобытие можно отклонить, только если оно еще не опубликовано");
            throw new EwmConflictException("Cобытие можно отклонить, только если оно еще не опубликовано");
        }
        return updateFieldsWithoutState(eventFromRepo, eventMapper.makeUpdateAdmin(updateRequestDto));
    }

    private Event updateFieldsWithoutState(Event eventFromRepo, EventFullDto makeUpdate) {

        if (makeUpdate.getPaid() != null && !eventFromRepo.getPaid().equals(makeUpdate.getPaid())) {
            eventFromRepo.setPaid(makeUpdate.getPaid());
        }

        if (makeUpdate.getEventDate() != null && !eventFromRepo.getEventDate().equals(makeUpdate.getEventDate())){
            eventFromRepo.setEventDate(makeUpdate.getEventDate());
        }

        if (makeUpdate.getAnnotation() != null && !eventFromRepo.getAnnotation().equals(makeUpdate.getAnnotation())){
            eventFromRepo.setAnnotation(makeUpdate.getAnnotation());
        }
        if (makeUpdate.getCategory().getId() != null
            && eventFromRepo.getCategory().getId() != (makeUpdate.getCategory().getId())){
            eventFromRepo.setCategory(categoryMapper.makeCategoryFromCategoryDto(makeUpdate.getCategory()));
        }
        if (makeUpdate.getDescription() != null
            && !eventFromRepo.getDescription().equals(makeUpdate.getDescription())){
            eventFromRepo.setDescription(makeUpdate.getDescription());
        }
        if (makeUpdate.getTitle() != null && !eventFromRepo.getTitle().equals(makeUpdate.getTitle())){
            eventFromRepo.setTitle(makeUpdate.getTitle());
        }
        if (makeUpdate.getLocation() != null
            && eventFromRepo.getLocation().getId() != makeUpdate.getLocation().getId()){
            eventFromRepo.setLocation(makeUpdate.getLocation());
        }
        if (makeUpdate.getParticipantLimit() != null
            && eventFromRepo.getParticipantLimit() != makeUpdate.getParticipantLimit()){
            eventFromRepo.setParticipantLimit(makeUpdate.getParticipantLimit());
        }
        if (makeUpdate.getRequestModeration() != null
            && eventFromRepo.getRequestModeration() != makeUpdate.getRequestModeration()){
            eventFromRepo.setRequestModeration(makeUpdate.getRequestModeration());
        }

        log.info("Event id {} ready for update", eventFromRepo.getId() );
        return eventFromRepo;
    }

    public Location saveLocation(NewEventDto dto) {
        return locationService.save(dto.getLocation());
    }

}


