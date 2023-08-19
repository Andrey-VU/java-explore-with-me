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
        log.info("{} EVENTS is FOUND", eventShortDtoList.size());
        return eventShortDtoList;
        //В случае, если по заданным фильтрам не найдено ни одного события, возвращает пустой список
    }

    @PostMapping("/events")
    @ResponseStatus(HttpStatus.CREATED)
    EventFullDto create(@Positive @PathVariable Long userId,
                        @RequestBody @Valid NewEventDto newEventDto) {
        log.info("CREATE EVENT: {}, by USER {}  - Started", newEventDto, userId);
        EventFullDto eventFullDto = eventService.create(userId, newEventDto);
        log.info("EVENT {} is CREATED", eventFullDto);
        return eventFullDto;
    }

    @GetMapping("/events/{eventId}")
    //Получение полной информации о событии добавленном текущим пользователем
    EventFullDto getPrivate(@Positive @PathVariable Long userId, @Positive @PathVariable Long eventId) {
        log.info("Try to find EVENT Id {} by USER Id {}", eventId, userId);
        EventFullDto eventFullDto = eventService.getFullDtoEvent(userId, eventId);
        log.info("{} EVENT is FOUND", eventFullDto);
        return eventFullDto;
    }

    @PatchMapping("/events/{eventId}")
    // изменить можно только отмененные события или события в состоянии ожидания модерации (Ожидается код ошибки 409)
    //дата и время на которые намечено событие не может быть раньше, чем через два часа от текущего момента (Ожидается код ошибки 409)
    EventFullDto updatePrivate(@Positive @PathVariable Long userId,
                               @Positive @PathVariable Long eventId,
                               @RequestBody @Valid NewEventDto newEventDto) {
        EventFullDto eventFullDto = eventService.updatePrivate(userId, eventId, newEventDto);
        log.info("EVENT was UPDATED: {}", eventFullDto);
        return eventFullDto;
    }

    @GetMapping("/events/{eventId}/requests")
    //Получение информации о запросах на участие в событии текущего пользователя
    List<ParticipationRequestDto> getRequestsPrivate(@Positive @PathVariable Long userId,
                                                     @Positive @PathVariable Long eventId) {
        List<ParticipationRequestDto> participationRequestDtoList = requestService.getRequestsListPrivate(userId, eventId);
        log.info("{} EVENTS is FOUND", participationRequestDtoList.size());
        return participationRequestDtoList;
        //В случае, если по заданным фильтрам не найдено ни одного запроса, возвращает пустой список
    }

    @PatchMapping("/events/{eventId}/requests")
    // Изменение статуса (подтверждена, отменена) заявок на участие в событии текущего пользователя
    EventRequestStatusUpdateResult updateRequestsPrivate(@Positive @PathVariable Long userId,
                                                         @Positive @PathVariable Long eventId,
                                                         @RequestBody @Valid EventRequestStatusUpdateRequest requestDto) {
        EventRequestStatusUpdateResult resultOfRequests = requestService.getResultRequestsListPrivate(userId, eventId);
        //log.info("{} EVENTS is FOUND", resultOfRequests.size());
        return resultOfRequests;

    //если для события лимит заявок равен 0 или отключена пре-модерация заявок, то подтверждение заявок не требуется
    //нельзя подтвердить заявку, если уже достигнут лимит по заявкам на данное событие (Ожидается код ошибки 409)
    //статус можно изменить только у заявок, находящихся в состоянии ожидания (Ожидается код ошибки 409)
    //если при подтверждении данной заявки, лимит заявок для события исчерпан,
    //то все неподтверждённые заявки необходимо отклонить

    }
}
