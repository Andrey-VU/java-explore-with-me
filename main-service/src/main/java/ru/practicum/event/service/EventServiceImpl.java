package ru.practicum.event.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.model.Category;
import ru.practicum.comments.dto.CommentDto;
import ru.practicum.comments.dto.query.QueryParamGetComments;
import ru.practicum.comments.model.Comment;
import ru.practicum.event.dto.*;
import ru.practicum.event.enums.EventState;
import ru.practicum.event.enums.SortBy;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.Location;
import ru.practicum.event.repo.EventRepo;
import ru.practicum.exception.EwmBadDataException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.user.model.User;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepo eventRepo;
    private final EventMapper eventMapper;
    private final EventMapperService mapperService;

    @Override
    @Transactional
    public EventFullDto updateAdmin(Long eventId, UpdateEventAdminRequest updateRequestDto) {

        Event eventFromRepo = eventRepo.findById(eventId)
            .orElseThrow(() -> new NotFoundException("Событие не найдено!"));

        if (updateRequestDto.getLocation() != null) {
            updateRequestDto.getLocation().setId(eventFromRepo.getLocation().getId());
        }
        Event preparedEventForUpdate = mapperService.prepareForAdminUpdate(eventFromRepo, updateRequestDto);
        Event updatedEvent = eventRepo.save(preparedEventForUpdate);

        Long views = mapperService.getViews(eventId);
        Long participants = mapperService.getParticipants(eventFromRepo);

        return eventMapper.makeFullDtoAddViewsAndParticipants(updatedEvent, views, participants);
    }

    @Override
    public List<EventFullDto> getAdmin(List<User> users, List<EventState> states, List<Category> categories,
                                       LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from, Integer size) {

        if (rangeStart != null && rangeEnd != null && rangeStart.isAfter(rangeEnd)) {
            log.warn("Указано некорректное время начала/окончания интервала");
            throw new EwmBadDataException("Задан некорректный временной интервал для поиска");
        }

        if (rangeStart == null) {
            rangeStart = LocalDateTime.now().minusYears(100);
        }
        if (rangeEnd == null) {
            rangeEnd = LocalDateTime.now().plusYears(100);
        }

        List<Event> events = new ArrayList<>();
        PageRequest pageRequest = PageRequest.of(from > 0 ? from / size : 0, size);
        List<Long> usersIds = new ArrayList<>();
        List<Long> categoriesIds = new ArrayList<>();

        if (users != null) {
            for (User user : users) {
                usersIds.add(user.getId());
            }
        }

        if (categories != null) {
            for (Category category : categories) {
                categoriesIds.add(category.getId());
            }
        }

        if (states == null) {
            states = List.of(EventState.PENDING);
        } else if (states.isEmpty()) {
            states.add(EventState.PENDING);
        }

        if (users == null && categories == null) {
            events = eventRepo.findAllByEventDateAfterAndEventDateBeforeAndStateIn(rangeStart, rangeEnd, states,
                pageRequest);
        } else if (users != null && categories == null) {
            events = eventRepo.findAllByEventDateAfterAndEventDateBeforeAndStateInAndInitiatorIdIn(rangeStart,
                rangeEnd, states, usersIds, pageRequest);
        }
        if (users != null && categories != null) {
            events =
                eventRepo.findAllByEventDateAfterAndEventDateBeforeAndStateInAndInitiatorIdInAndCategoryIdIn(rangeStart,
                    rangeEnd, states, usersIds, categoriesIds, pageRequest);
        } else if (users == null && categories != null) {
            events = eventRepo.findAllByEventDateAfterAndEventDateBeforeAndStateInAndCategoryIdIn(rangeStart,
                rangeEnd, states, categoriesIds, pageRequest);
        }


        log.info("Найдено {} событий в соответствии с заданными критериями", events.size());

        return events.stream()
            .map(event -> eventMapper.makeFullDtoAddViewsAndParticipants(event, mapperService.getViews(event.getId()),
                mapperService.getParticipants(event)))
            .collect(Collectors.toList());
    }

    @Override
    public List<EventFullDto> getPublicEvents(String text, Boolean paid, List<Category> categories,
                                              LocalDateTime rangeStart, LocalDateTime rangeEnd, Boolean onlyAvailable,
                                              SortBy sort, Integer from, Integer size, HttpServletRequest request) {
        if (rangeStart != null && rangeEnd != null && rangeStart.isAfter(rangeEnd)) {
            log.warn("Указано некорректное время начала/окончания интервала");
            throw new EwmBadDataException("Задан некорректный временной интервал для поиска");
        }

        if (rangeStart == null) {
            rangeStart = LocalDateTime.now().minusYears(100);
        }
        if (rangeEnd == null) {
            rangeEnd = LocalDateTime.now().plusYears(100);
        }

        List<Event> events = new ArrayList<>();

        PageRequest pageRequest = PageRequest.of(from > 0 ? from / size : 0, size);
        List<Long> categoriesIds = new ArrayList<>();

        if (categories != null) {
            for (Category category : categories) {
                categoriesIds.add(category.getId());
            }
        }
        events = eventRepo.getEventsPublic(text, categoriesIds, paid, rangeStart, rangeEnd, pageRequest);

        mapperService.saveStatistics(request);

        log.info("PUBLIC ACCESS. Найдено {} событий в соответствии с заданными критериями", events.size());

        return events.stream()
            .map(event -> eventMapper.makeFullDtoAddViewsAndParticipants(event, mapperService.getViews(event.getId()),
                mapperService.getParticipants(event)))
            .collect(Collectors.toList());
    }

    @Override
    public EventFullDto getPublic(Long eventId, HttpServletRequest request) {
        Event event = eventRepo.findByIdAndPublishedOnBefore(eventId, LocalDateTime.now())
            .orElseThrow(() -> new NotFoundException("Событие id " + eventId + " - не найдено"));

        log.info("PUBLIC ACCESS. Найдено событие id {}", eventId);

        Long views = mapperService.getViews(event.getId());
        Long participants = mapperService.getParticipants(event);
        EventFullDto fullDto = eventMapper.makeFullDtoAddViewsAndParticipants(event, views, participants);
        mapperService.saveStatistics(request);

        log.info("PUBLIC ACCESS. Статистика о просмотре события id {} отправлена в StatService", eventId);

        return fullDto;
    }

    @Override
    public List<EventShortDto> getListPrivate(Long initiatorId, Integer from, Integer size) {
        List<EventShortDto> eventShortDtoList = new ArrayList<>();
        PageRequest pageRequest = PageRequest.of(from > 0 ? from / size : 0, size);
        eventShortDtoList = eventRepo.findAllByInitiatorId(initiatorId, pageRequest).stream()
            .map(event -> eventMapper.makeShortDto(event))
            .collect(Collectors.toList());

        log.info("PRIVATE ACCESS. Найдено {} событий, инициированных пользователем Id {}", eventShortDtoList.size(),
            initiatorId);
        return eventShortDtoList;
    }

    @Override
    @Transactional
    public EventFullDto create(Long initiatorId, NewEventDto dto) {
        User initiator = mapperService.makeUser(initiatorId);
        Category category = mapperService.makeCategory(dto.getCategory());
        Location locationFromDB = mapperService.saveLocation(dto);
        dto.setLocation(locationFromDB);

        mapperService.isEventDateValid(dto.getEventDate());

        Event newEvent = eventRepo.save(eventMapper.makeEvent(dto, initiator, category, LocalDateTime.now()));
        log.info("PRIVATE ACCESS. {} - CREATED", eventMapper.makeShortDto(newEvent));
        return eventMapper.makeFullDtoAddViewsAndParticipants(newEvent, null, null);
    }

    @Override
    public EventFullDto getFullDtoEventPrivate(Long initiatorId, Long eventId) {
        Event eventFromRepo = eventRepo.findById(eventId)
            .orElseThrow(() -> new NotFoundException("Event Id " + eventId + "is NOT FOUND!"));

        Long views = mapperService.getViews(eventId);
        Long participants = mapperService.getParticipants(eventFromRepo);

        EventFullDto fullDto = eventMapper.makeFullDtoAddViewsAndParticipants(eventFromRepo, views, participants);
        isUserTheInitiator(initiatorId, fullDto);
        log.info("PRIVATE ACCESS. Event {} is FOUND!", fullDto);
        return fullDto;
    }

    @Override
    @Transactional
    public EventFullDto updatePrivate(Long initiatorId, Long eventId, UpdateEventUserRequest updateForEvent) {
        Event eventFromRepo = eventMapper.makeEventFromFullDto(getFullDtoEventPrivate(initiatorId, eventId));
        Event prepareForUpdate = mapperService.prepareForUpdate(eventFromRepo, updateForEvent);

        Event updatedEvent = eventRepo.save(prepareForUpdate);
        log.info("PRIVATE ACCESS. {} - UPDATED", eventMapper.makeShortDto(updatedEvent));
        return eventMapper.makeFullDto(updatedEvent);
    }

    private void isUserTheInitiator(Long initiatorId, EventFullDto fullDto) {
        if (!initiatorId.equals(fullDto.getInitiator().getId())) {
            log.warn("Указанный Id {} не соответствует инициатору события {}", initiatorId, fullDto);
            throw new NotFoundException("Не найдено события c id " + fullDto.getId()
                + ", инициированного пользователем c id" + initiatorId);
        }
    }
}
