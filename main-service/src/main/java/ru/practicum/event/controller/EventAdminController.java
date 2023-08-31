package ru.practicum.event.controller;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.category.model.Category;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.UpdateEventAdminRequest;
import ru.practicum.event.enums.EventState;
import ru.practicum.event.service.EventService;
import ru.practicum.user.model.User;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.utils.MainConstants.DATE_TIME_FORMAT;

@Validated
@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping(path = "/admin/events")
public class EventAdminController {
    EventService eventService;

    @GetMapping
    List<EventFullDto> getAdmin(@RequestParam(required = false) List<User> users,
                                @RequestParam(required = false) List<EventState> states,
                                @RequestParam(required = false) List<Category> categories,
                  @RequestParam(required = false) @DateTimeFormat(pattern = DATE_TIME_FORMAT) LocalDateTime rangeStart,
                  @RequestParam(required = false) @DateTimeFormat(pattern = DATE_TIME_FORMAT) LocalDateTime rangeEnd,
                  @PositiveOrZero @RequestParam (required = false, defaultValue = "0") Integer from,
                  @Positive @RequestParam (required = false, defaultValue = "10") Integer size) {

        List<EventFullDto> fullDtoList = eventService.getAdmin(users, states, categories, rangeStart, rangeEnd, from, size);
        log.info("Найдено {} событий", fullDtoList.size());
        return fullDtoList;
    }

    @PatchMapping("/{eventId}")
    EventFullDto updateAdmin(@Positive @PathVariable Long eventId,
                             @Valid @RequestBody UpdateEventAdminRequest updateRequest){
        return eventService.updateAdmin(eventId, updateRequest);
    }
}
