package ru.practicum.comments.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comments.dto.CommentDto;
import ru.practicum.comments.dto.NewCommentDto;
import ru.practicum.comments.service.CommentService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

@Validated
@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping(path = "/comments/{eventId}")
public class PrivateCommentsController {
    private final CommentService commentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    CommentDto createPrivate(@Positive @PathVariable Long eventId,
                             @Positive @RequestParam Long userId,
                             @Valid @RequestBody NewCommentDto newCommentDto) {
        log.info("CREATE Comment for EVENT Id: {}, by USER {}  - Started", eventId, userId);
        CommentDto commentDto = commentService.create(userId, eventId, newCommentDto);
        log.info("PRIVATE ACCESS. COMMENT {} is CREATED", commentDto);
        return commentDto;
    }

    @PatchMapping(path = "/{commentId}")
    CommentDto update(@Positive @PathVariable Long eventId,
                      @Positive @PathVariable Long commentId,
                      @Positive @RequestParam Long userId,
                      @Valid @RequestBody NewCommentDto newCommentDto) {
        log.info("UPDATE Comment for EVENT Id: {}, by USER {}  - Started", commentId, userId);
        CommentDto commentDto = commentService.privateUpdate(userId, commentId, eventId, newCommentDto);
        log.info("PRIVATE ACCESS. Update {} is done", commentDto);
        return commentDto;
    }


    @DeleteMapping(path = "/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteComment(@Positive @PathVariable Long eventId,
                       @Positive @PathVariable Long commentId,
                       @Positive @RequestParam Long userId) {
        log.info("Принят запрос на удаление комментария к событию {}", commentId);
        commentService.privateDelete(eventId, commentId, userId);
    }

}
