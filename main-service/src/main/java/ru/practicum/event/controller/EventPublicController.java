package ru.practicum.event.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.category.model.Category;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.enums.SortBy;
import ru.practicum.event.service.EventService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.utils.MainConstants.DATE_TIME_FORMAT;

@Validated
@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping(path = "/events")
public class EventPublicController {
    EventService eventService;

    @GetMapping
    List<EventFullDto> getEvents(@RequestParam(required = false) String text,
                                 @RequestParam(required = false) Boolean paid,
                                 @RequestParam(required = false) List<Category> categories,
                                 @RequestParam(required = false) @DateTimeFormat(pattern = DATE_TIME_FORMAT)
                                 LocalDateTime rangeStart,
                                 @RequestParam(required = false) @DateTimeFormat(pattern = DATE_TIME_FORMAT)
                                 LocalDateTime rangeEnd,
                                 @RequestParam(defaultValue = "false") Boolean onlyAvailable,
                                 @RequestParam(required = false) SortBy sort,
                                 @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                 @Positive @RequestParam(defaultValue = "10") Integer size,
                                 HttpServletRequest request) {

        log.info(
            "PUBLIC ACCESS. EventPublicController: Получен запрос с ip адреса {} на просмотр всех событий по фильтрам: \n" +
                "- text: {};\n" +
                "- categories: {};\n" +
                "- paid: {};\n" +
                "- rangeStart: {};\n" +
                "- rangeEnd: {};\n" +
                "- onlyAvailable: {};\n" +
                "- sort: {};\n" +
                "- from: {};\n" +
                "- size: {}.",
            request.getRemoteAddr(), text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);

        List<EventFullDto> foundEvents =
            eventService.getPublicEvents(text, paid, categories, rangeStart, rangeEnd, onlyAvailable, sort, from,
                size, request);

        log.info("Найдено {} событий", foundEvents.size());

        return foundEvents;
    }

    @GetMapping("/{id}")
    EventFullDto get(@PathVariable Long id, HttpServletRequest request) {
        EventFullDto eventFullDto = eventService.getPublic(id, request);
        log.info("PUBLIC ACCESS. Найдено событие: {}", eventFullDto);
        return eventFullDto;
    }
}