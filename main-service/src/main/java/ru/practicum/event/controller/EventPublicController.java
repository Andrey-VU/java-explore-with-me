package ru.practicum.event.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.category.model.Category;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.enums.SortBy;
import ru.practicum.utils.MainConstants;

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

    @GetMapping
    List<EventFullDto> getEvents(@RequestParam(required = false) String text, //maxLength: 7000, minLength: 1
                                 @RequestParam(required = false) Boolean paid,
                                 @RequestParam(required = false) List<Category> categories,
        @RequestParam(required = false) @DateTimeFormat(pattern = DATE_TIME_FORMAT) LocalDateTime rangeStart,
        @RequestParam(required = false) @DateTimeFormat(pattern = DATE_TIME_FORMAT) LocalDateTime rangeEnd,
        @RequestParam(defaultValue = "false") Boolean onlyAvailable,
        @RequestParam(required = false) SortBy sort,
        @PositiveOrZero @RequestParam (defaultValue = "0") Integer from,
        @Positive @RequestParam (defaultValue = "10") Integer size) {



        return null;
    }
}
