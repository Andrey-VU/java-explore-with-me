package ru.practicum.event.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.category.model.Category;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.dto.UpdateEventAdminRequest;
import ru.practicum.event.enums.EventState;
import ru.practicum.event.enums.SortBy;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.Event;
import ru.practicum.event.repo.EventRepo;
import ru.practicum.exception.NotFoundException;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.user.model.User;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class EventServiceImpl implements EventService{
    private final EventRepo eventRepo;
    private final EventMapper eventMapper;
    private final EventMapperService mapperService;

    //Жизненный цикл события должен включать несколько этапов.
    //Создание.
    //Ожидание публикации. В статус ожидания публикации событие переходит сразу после создания.
    //Публикация. В это состояние событие переводит администратор.
    //Отмена публикации. В это состояние событие переходит в двух случаях.
    // Первый — если администратор решил, что его нельзя публиковать.
    // Второй — когда инициатор события решил отменить его на этапе ожидания публикации.
    //Чтобы избежать распространённых ошибок при работе с моделью данных,
    // применяйте знания из урока о подготовке к взаимодействию с БД.

    @Override
    public List<EventFullDto> getAdmin(List<User> users, List<EventState> states, List<Category> categories,
                                       LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from, Integer size) {
        //Эндпоинт возвращает полную информацию обо всех событиях подходящих под переданные условия
        //В случае, если по заданным фильтрам не найдено ни одного события, возвращает пустой список
        List<EventFullDto> eventsForAdmin = new ArrayList<>();
        PageRequest pageRequest = PageRequest.of(from > 0 ? from / size : 0, size);



        return eventsForAdmin;
    }

    @Override
    public EventFullDto updateAdmin(Long eventId, UpdateEventAdminRequest updateRequestDto) {

        return null;
    }
// // Публикация. В это состояние событие переводит администратор.
// // Отмена публикации. В это состояние событие переходит в двух случаях.
// // Первый — если администратор решил, что его нельзя публиковать.
// // Второй — когда инициатор события решил отменить его на этапе ожидания публикации.

    @Override
    public List<EventFullDto> getPublicEvents(String text, Boolean paid, List<Category> categories,
                                              LocalDateTime rangeStart, LocalDateTime rangeEnd, Boolean onlyAvailable,
                                              SortBy sort, Integer from, Integer size) {
        return null;
    }

    @Override
    public EventFullDto getPublic(Long eventId, HttpServletRequest request) {
        Event event = eventRepo.findByIdAndPublishedOnBefore(eventId, LocalDateTime.now())
            .orElseThrow(() -> new NotFoundException("Событие id " + eventId + " - не найдено"));

        Long views = mapperService.addViews(event.getId());
        Long participants = mapperService.getParticipants(event.getId());
        EventFullDto fullDto = eventMapper.makeFullDtoAddViewsAndParticipants(event, views, participants);
        log.info("Найдено событие {}", fullDto);
        mapperService.saveStatistics(request);
        log.info("Статистика о просмотре события id {} отправлена в StatService", eventId);
        return fullDto;
    }

    @Override
    public List<EventShortDto> getListPrivate(Long initiatorId, Integer from, Integer size) {
        List<EventShortDto> eventShortDtoList = new ArrayList<>();
        PageRequest pageRequest = PageRequest.of(from > 0 ? from / size : 0, size);
        eventShortDtoList = eventRepo.findAllByInitiatorId(initiatorId, pageRequest).stream()
            .map(event -> eventMapper.makeShortDto(event))
            .collect(Collectors.toList());

        log.info("Найдено {} событий, инициированных пользователем Id {}", eventShortDtoList.size(), initiatorId );
        return eventShortDtoList;
    }

    @Override
    public EventFullDto create(Long initiatorId, NewEventDto dto) {
        User initiator = mapperService.makeUser(initiatorId);
        Category category = mapperService.makeCategory(dto.getCategory());
        mapperService.saveLocation(dto);

        Event newEvent = eventRepo.save(eventMapper.makeEvent(dto, initiator, category));
        log.info("{} - CREATED", eventMapper.makeShortDto(newEvent));
        return eventMapper.makeFullDtoAddViewsAndParticipants(newEvent, null, null);
    }

    @Override
    public EventFullDto getFullDtoEventPrivate(Long initiatorId, Long eventId) {
        Event eventFromRepo = eventRepo.findById(eventId)
            .orElseThrow(() -> new NotFoundException("Event Id " + eventId + "is NOT FOUND!"));

        Long views = mapperService.addViews(eventId);
        Long participants = mapperService.getParticipants(eventId);

        EventFullDto fullDto = eventMapper.makeFullDtoAddViewsAndParticipants(eventFromRepo, views, participants);
        isUserTheInitiator(initiatorId, fullDto);
        log.info("Event {} is FOUND!", fullDto);
        return fullDto;
    }

    @Override
    public EventFullDto updatePrivate(Long initiatorId, Long eventId, EventFullDto updateForEvent) {
        Event eventFromRepo = eventMapper.makeEventFromFullDto(getFullDtoEventPrivate(initiatorId, eventId));
        Event prepareForUpdate = mapperService.prepareForUpdate(eventFromRepo, updateForEvent);
        Event updatedEvent = eventRepo.save(prepareForUpdate);
        log.info("{} - UPDATED", eventMapper.makeShortDto(updatedEvent));
        return eventMapper.makeFullDto(updatedEvent);
    }

    @Override
    public List<ParticipationRequestDto> getRequestsListPrivate(Long userId, Long eventId) {
        return null;
    }

    @Override
    public Event getEventById(Long eventId) {
        return eventRepo.findById(eventId).orElseThrow(() -> new NotFoundException("Событие " + eventId
            + " не найдено!"));
    }

    private void isUserTheInitiator(Long initiatorId, EventFullDto fullDto) {
        if (initiatorId != fullDto.getInitiator().getId()) {
            log.warn("Указанный Id {} не соответствует инициатору события {}", initiatorId, fullDto);
            throw new NotFoundException("Не найдено события c id " + fullDto.getId()
                + ", инициированного пользователем c id" + initiatorId);
        }
    }
}
