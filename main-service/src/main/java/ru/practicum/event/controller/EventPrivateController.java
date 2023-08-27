package ru.practicum.event.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.event.service.EventService;
import ru.practicum.request.service.RequestService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;


@Validated
@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping("/users/{userId}")
public class EventPrivateController {
    EventService eventService;
    RequestService requestService;

    // авторизованные пользователи должны иметь возможность добавлять в приложение новые мероприятия,
    // редактировать их и просматривать после добавления;
    // должна быть настроена подача заявок на участие в интересующих мероприятиях;
    // создатель мероприятия должен иметь возможность подтверждать заявки,
    // которые отправили другие пользователи сервиса.

    @GetMapping("/events")
    List<EventShortDto> getListPrivate(@Positive @PathVariable Long userId,
                                       @PositiveOrZero @RequestParam (required = false, defaultValue = "0") Integer from,
                                       @Positive @RequestParam (required = false, defaultValue = "10") Integer size) {
        List<EventShortDto> eventShortDtoList = eventService.getListPrivate(userId, from, size);
        log.info("PRIVATE ACCESS. {} EVENTS is FOUND", eventShortDtoList.size());
        return eventShortDtoList;
    }

    @PostMapping("/events")
    @ResponseStatus(HttpStatus.CREATED)
    EventFullDto createPrivate(@Positive @PathVariable Long userId,
                               @RequestBody @Valid NewEventDto newEventDto) {
        log.info("CREATE EVENT: {}, by USER {}  - Started", newEventDto, userId);
        EventFullDto eventFullDto = eventService.create(userId, newEventDto);
        log.info("PRIVATE ACCESS. EVENT {} is CREATED", eventFullDto);
        return eventFullDto;
    }

    @GetMapping("/events/{eventId}")
    //Получение полной информации о событии добавленном текущим пользователем
    EventFullDto getPrivate(@Positive @PathVariable Long userId, @Positive @PathVariable Long eventId) {
        log.info("Try to find EVENT Id {} by USER Id {}", eventId, userId);
        EventFullDto eventFullDto = eventService.getFullDtoEventPrivate(userId, eventId);
        log.info("PRIVATE ACCESS. {} EVENT is FOUND", eventFullDto);
        return eventFullDto;
    }

    @PatchMapping("/events/{eventId}")
    EventFullDto updatePrivate(@Positive @PathVariable Long userId,
                               @Positive @PathVariable Long eventId,
                               @RequestBody @Valid EventFullDto updateForEvent) {
        EventFullDto eventFullDto = eventService.updatePrivate(userId, eventId, updateForEvent);
        log.info("PRIVATE ACCESS. EVENT was UPDATED: {}", eventFullDto);
        return eventFullDto;
    }

    @GetMapping("/events/{eventId}/requests")
    //Получение информации о запросах на участие в событии текущего пользователя
    List<ParticipationRequestDto> getRequestsPrivate(@Positive @PathVariable Long userId,
                                                     @Positive @PathVariable Long eventId) {
        List<ParticipationRequestDto> participationRequestDtoList = requestService.getRequestsListPrivate(userId, eventId);
        log.info("PRIVATE ACCESS. {} EVENTS is FOUND", participationRequestDtoList.size());

        // НЕ РЕАЛИЗОВАНО

        return participationRequestDtoList;
        //В случае, если по заданным фильтрам не найдено ни одного запроса, возвращает пустой список
    }

    @PatchMapping("/events/{eventId}/requests")
    // Изменение статуса (подтверждена, отменена) заявок на участие в событии текущего пользователя
    EventRequestStatusUpdateResult updateRequestsPrivate(@Positive @PathVariable Long userId,
                                                         @Positive @PathVariable Long eventId,
                                                         @RequestBody @Valid EventRequestStatusUpdateRequest requestDto) {
        EventRequestStatusUpdateResult resultOfRequests
            = requestService.updateResultRequestsListPrivate(userId, eventId, requestDto);
        log.info("PRIVATE ACCESS. {} WAS CONFIRMED & {} WAS DECLINED", resultOfRequests.getConfirmedRequests().size(),
            resultOfRequests.getRejectedRequests().size());
        return resultOfRequests;
    }
}
