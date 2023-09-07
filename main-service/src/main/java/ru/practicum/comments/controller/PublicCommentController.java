package ru.practicum.comments.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.comments.dto.CommentDto;
import ru.practicum.comments.dto.query.QueryParamGetComments;
import ru.practicum.comments.service.CommentService;
import ru.practicum.event.service.EventService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Validated
@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping(path = "/comments/public")
public class PublicCommentController {
    CommentService commentService;

    @GetMapping
    List<CommentDto> getComments(@PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                 @Positive @RequestParam(defaultValue = "10") Integer size,
                                 @Valid QueryParamGetComments queryParams) {
        log.info("Получен запрос на выгрузку комментариев: {}, " +
            " - from: {}\n" +
            " - size: {}\n", queryParams.toString(), from, size);
        return commentService.getComments(queryParams, from, size);
    }
}
