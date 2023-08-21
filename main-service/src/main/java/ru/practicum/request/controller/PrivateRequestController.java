package ru.practicum.request.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.service.RequestService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

@Validated
@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping("/users/{userId}/requests")
public class PrivateRequestController {
    private final RequestService requestService;

    @GetMapping
    List<ParticipationRequestDto> get(@Positive @PathVariable Long userId) {
        log.info("Принят запрос на получение списка всех заявок пользователя Id {}", userId);
        return requestService.get(userId);
    }

    @PostMapping
    ParticipationRequestDto create(@Positive @PathVariable Long requesterId,
                                   @Valid @RequestBody ParticipationRequestDto requestDto){
        log.info("Принят запрос от пользователя Id {} на создание заявки для участие в мероприятии Id {}",
            requesterId, requestDto.getEventId());
        return requestService.create(requesterId, requestDto);
    }

    @PatchMapping("/{requestId}/cancel")
    ParticipationRequestDto requestCancel(@Positive @PathVariable Long userId,
                                          @Positive @PathVariable Long requestId){
        log.info("Принят запрос от пользователя Id {} на отмену заявки Id {}", userId, requestId);
        return requestService.cancel(userId, requestId);
    }


}
