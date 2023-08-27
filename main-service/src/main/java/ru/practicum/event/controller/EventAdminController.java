package ru.practicum.event.controller;

import lombok.AllArgsConstructor;
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

    // нужно настроить добавление, изменение и удаление категорий для событий;
    // должна появиться возможность добавлять, удалять и закреплять на главной странице подборки мероприятий;
    // требуется наладить модерацию событий, размещённых пользователями, — публикация или отклонение;
    // также должно быть настроено управление пользователями — добавление, активация, просмотр и удаление.

    @GetMapping
    List<EventFullDto> getAdmin(@RequestParam(required = false) List<User> users,
                                @RequestParam(required = false) List<EventState> states,
                                @RequestParam(required = false) List<Category> categories,
                  @RequestParam(required = false) @DateTimeFormat(pattern = DATE_TIME_FORMAT) LocalDateTime rangeStart,
                  @RequestParam(required = false) @DateTimeFormat(pattern = DATE_TIME_FORMAT) LocalDateTime rangeEnd,
                  @PositiveOrZero @RequestParam (required = false, defaultValue = "0") Integer from,
                  @Positive @RequestParam (required = false, defaultValue = "10") Integer size) {

        return eventService.getAdmin(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    @PatchMapping("/{eventId}")
    EventFullDto updateAdmin(@Positive @PathVariable Long eventId,
                             @Valid @RequestBody UpdateEventAdminRequest updateRequest){
        return eventService.updateAdmin(eventId, updateRequest);
    }


    //Редактирование данных любого события администратором. Валидация данных не требуется. Обратите внимание:
    //дата начала изменяемого события должна быть не ранее чем за час от даты публикации. (Ожидается код ошибки 409)
    //событие можно публиковать, только если оно в состоянии ожидания публикации (Ожидается код ошибки 409)
    //событие можно отклонить, только если оно еще не опубликовано (Ожидается код ошибки 409)
}
