package ru.practicum.event.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.category.model.Category;
import ru.practicum.category.model.CategoryService;
import ru.practicum.category.repo.CategoryRepo;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.dto.UpdateEventAdminRequest;
import ru.practicum.event.dto.UpdateEventUserRequest;
import ru.practicum.event.enums.EventState;
import ru.practicum.event.enums.StateActionAdmin;
import ru.practicum.event.enums.StateActionUser;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.Location;
import ru.practicum.exception.EwmBadDataException;
import ru.practicum.exception.EwmConflictException;
import ru.practicum.exception.NotFoundException;
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
    private final UserMapper userMapper;
    private final RequestService requestService;
    private final CategoryRepo categoryRepo;

    User makeUser(Long userId) {
        return userMapper.makeUserFromDto(userService.get(userId));
    }

    Long getViews(Long eventId) {
        Long views = viewService.getViewsById(eventId);
        log.info("Получена информация от StatsService: Событие Id {} было просмотрено {} раз", eventId, views);
        return views;
    }

    public Long getParticipants(Event event) {
        return requestService.getConfirmedRequests(event);
    }

    public void saveStatistics(HttpServletRequest request) {
        viewService.saveHit(request.getRequestURI(), request.getLocalAddr());
    }

    public Event prepareForUpdate(Event eventFromRepo, UpdateEventUserRequest updateForEvent) {

        if (eventFromRepo.getState().equals(EventState.PUBLISHED)) {
            log.warn("Событие не может быть обновлено. Конфликт состояния");
            throw new EwmConflictException("изменить можно только отмененные события или события в состоянии " +
                "ожидания модерации");
        }
        if (updateForEvent.getStateAction() != null
            && updateForEvent.getStateAction().equals(StateActionUser.CANCEL_REVIEW)) {
            eventFromRepo.setState(EventState.CANCELED);
        } else if (updateForEvent.getStateAction() != null
            && updateForEvent.getStateAction().equals(StateActionUser.SEND_TO_REVIEW)) {
            eventFromRepo.setState(EventState.PENDING);
        }

        if (updateForEvent.getEventDate() != null &&
            updateForEvent.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            log.warn("Событие не может быть обновлено. Конфликт дат");
            throw new EwmBadDataException("Дата и время на которые намечено событие не может быть раньше, чем " +
                "через два часа от текущего момента");
        }
        return updateFieldsWithoutState(eventFromRepo, updateForEvent);
    }

    public Category makeCategory(Long category) {
        return categoryMapper.makeCategoryFromCategoryDto(categoryService.getPublic(category));
    }

    public Event prepareForAdminUpdate(Event eventFromRepo, UpdateEventAdminRequest updateRequestDto) {

        if (updateRequestDto.getEventDate()!= null && eventFromRepo.getPublishedOn() != null
            && !updateRequestDto.getEventDate().minusHours(1).isBefore(eventFromRepo.getPublishedOn())) {
            log.warn("Событие не может быть обновлено. Конфликт дат");
            throw new EwmBadDataException("Изменяемое событие должно начинаться позже, чем через час после публикации!");
        }
        if (updateRequestDto.getEventDate()!= null &&
            updateRequestDto.getEventDate().minusHours(2).isBefore(LocalDateTime.now())) {
            log.warn("Событие не может быть обновлено. Конфликт дат");
            throw new EwmBadDataException("Начало события должно быть позже, чем через 2 часа от данного момента!");
        }

        if (!eventFromRepo.getState().equals(EventState.PENDING)) {
            log.warn("Событие не может быть опубликовано. Конфликт состояния");
            throw new EwmConflictException("Опубликовать можно события в статусе ожидания публикации");
        }
        if (updateRequestDto.getStateAction() !=null
            && updateRequestDto.getStateAction().equals(StateActionAdmin.REJECT_EVENT)
            && eventFromRepo.getState().equals(EventState.PUBLISHED)
            || eventFromRepo.getState().equals(EventState.CANCELED)) {
            log.warn("Cобытие можно отклонить, только если оно еще не опубликовано");
            throw new EwmConflictException("Cобытие не опубликовано. Отклонение не применимо");
        }
        return makeAdminUpdate(eventFromRepo, updateRequestDto);
    }

    private Event makeAdminUpdate(Event eventFromRepo, UpdateEventAdminRequest updateRequestDto) {
        if (updateRequestDto.getStateAction() != null
            && updateRequestDto.getStateAction().equals(StateActionAdmin.REJECT_EVENT)) {
            eventFromRepo.setState(EventState.CANCELED);
        } else if (updateRequestDto.getStateAction() != null
            && updateRequestDto.getStateAction().equals(StateActionAdmin.PUBLISH_EVENT)) {
            eventFromRepo.setState(EventState.PUBLISHED);
            eventFromRepo.setPublishedOn(LocalDateTime.now());
        }

        if (updateRequestDto.getPaid() != null && !eventFromRepo.getPaid().equals(updateRequestDto.getPaid())) {
            eventFromRepo.setPaid(updateRequestDto.getPaid());
        }
        if (updateRequestDto.getEventDate() != null && !eventFromRepo.getEventDate().equals(updateRequestDto.getEventDate())){
            eventFromRepo.setEventDate(updateRequestDto.getEventDate());
        }
        if (updateRequestDto.getAnnotation() != null && !eventFromRepo.getAnnotation().equals(updateRequestDto.getAnnotation())){
            eventFromRepo.setAnnotation(updateRequestDto.getAnnotation());
        }
        if (updateRequestDto.getCategory() != null
            && !eventFromRepo.getCategory().getId().equals(updateRequestDto.getCategory())){
            eventFromRepo.setCategory(categoryRepo.findById(updateRequestDto.getCategory())
                .orElseThrow(() -> new NotFoundException("Category id " + updateRequestDto.getCategory()
                    + "НЕ ОБНАРУЖЕНА!")));
        }
        if (updateRequestDto.getDescription() != null
            && !eventFromRepo.getDescription().equals(updateRequestDto.getDescription())){
            eventFromRepo.setDescription(updateRequestDto.getDescription());
        }
        if (updateRequestDto.getTitle() != null && !eventFromRepo.getTitle().equals(updateRequestDto.getTitle())){
            eventFromRepo.setTitle(updateRequestDto.getTitle());
        }
        if (updateRequestDto.getLocation() != null
            && !eventFromRepo.getLocation().equals(updateRequestDto.getLocation())){
            eventFromRepo.setLocation(updateRequestDto.getLocation());
        }
        if (updateRequestDto.getParticipantLimit() != null
            && !eventFromRepo.getParticipantLimit().equals(updateRequestDto.getParticipantLimit())){
            eventFromRepo.setParticipantLimit(updateRequestDto.getParticipantLimit());
        }
        if (updateRequestDto.getRequestModeration() != null
            && eventFromRepo.getRequestModeration() != updateRequestDto.getRequestModeration()){
            eventFromRepo.setRequestModeration(updateRequestDto.getRequestModeration());
        }

        log.info("ADMIN ACCESS. Event id {} ready for update", eventFromRepo.getId() );
        return eventFromRepo;

    }

     private Event updateFieldsWithoutState(Event eventFromRepo, UpdateEventUserRequest makeUpdate) {

        if (makeUpdate.getPaid() != null && !eventFromRepo.getPaid().equals(makeUpdate.getPaid())) {
            eventFromRepo.setPaid(makeUpdate.getPaid());
        }

        if (makeUpdate.getEventDate() != null && !eventFromRepo.getEventDate().equals(makeUpdate.getEventDate())){
            eventFromRepo.setEventDate(makeUpdate.getEventDate());
        }

        if (makeUpdate.getAnnotation() != null && !eventFromRepo.getAnnotation().equals(makeUpdate.getAnnotation())){
            eventFromRepo.setAnnotation(makeUpdate.getAnnotation());
        }

        if (makeUpdate.getCategory() != null
            && !eventFromRepo.getCategory().getId().equals(makeUpdate.getCategory())){

            eventFromRepo.setCategory(categoryRepo.findById(makeUpdate.getCategory())
                .orElseThrow(() -> new NotFoundException("Искомая категория не обнаружена")));
        }

        if (makeUpdate.getDescription() != null
            && !eventFromRepo.getDescription().equals(makeUpdate.getDescription())){
            eventFromRepo.setDescription(makeUpdate.getDescription());
        }

        if (makeUpdate.getTitle() != null && !eventFromRepo.getTitle().equals(makeUpdate.getTitle())){
            eventFromRepo.setTitle(makeUpdate.getTitle());
        }

        if (makeUpdate.getLocation() != null
            && !eventFromRepo.getLocation().equals(makeUpdate.getLocation())){
            eventFromRepo.setLocation(makeUpdate.getLocation());
        }

        if (makeUpdate.getParticipantLimit() != null
            && !eventFromRepo.getParticipantLimit().equals(makeUpdate.getParticipantLimit())){
            eventFromRepo.setParticipantLimit(makeUpdate.getParticipantLimit());
        }

        if (makeUpdate.getRequestModeration() != null
            && eventFromRepo.getRequestModeration() != makeUpdate.getRequestModeration()){
            eventFromRepo.setRequestModeration(makeUpdate.getRequestModeration());
        }

        log.info("Event id {} ready for update", eventFromRepo.getId());

        return eventFromRepo;
    }

    public Location saveLocation(NewEventDto dto) {
        return locationService.save(dto.getLocation());
    }

    public void isEventDateValid(LocalDateTime eventDate) {
        if (eventDate.isBefore(LocalDateTime.now().plusHours(2))) {
            throw new EwmBadDataException("Начало событие должно быть хотя бы на 2 часа позднее настоящего момента");
        }
    }
}


