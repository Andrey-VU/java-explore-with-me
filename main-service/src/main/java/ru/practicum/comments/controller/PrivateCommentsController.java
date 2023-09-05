package ru.practicum.comments.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comments.dto.CommentDto;
import ru.practicum.comments.dto.NewCommentDto;
import ru.practicum.comments.service.CommentService;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.NewEventDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

@Validated
@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping("/comments/{eventId}")
public class PrivateCommentsController {
    CommentService commentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    CommentDto createPrivate(@Positive @PathVariable Long eventId,
                             @Positive @RequestParam Long userId,
                             @Valid @RequestBody NewCommentDto newCommentDto) {
        log.info("CREATE Comment for EVENT Id: {}, by USER {}  - Started", eventId, userId);
        CommentDto commentDto = commentService.create(userId, eventId, newCommentDto);
        log.info("PRIVATE ACCESS. EVENT {} is CREATED", commentDto);
        return commentDto;
    }

}
