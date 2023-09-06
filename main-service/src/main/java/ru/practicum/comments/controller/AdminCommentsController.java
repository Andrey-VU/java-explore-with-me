package ru.practicum.comments.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comments.dto.CommentDto;
import ru.practicum.comments.mapper.CommentMapper;
import ru.practicum.comments.service.CommentService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

@Validated
@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping("admin/comments")
public class AdminCommentsController {
    CommentService commentService;
    CommentMapper commentMapper;

    @PatchMapping
    void adminModerateComment(@RequestBody CommentDto commentDto) {
        log.info("Comment for EVENT Id: {}, принято на модерацию", commentDto.getEventId());
        commentService.adminModerate(commentDto);
    }

    @PatchMapping
    void adminTurnToPending(@Positive @RequestParam Long commentId) {
        log.info("Комментарий id: {} помечен как 'токсичный' и снят с публикации", commentId);
        commentService.adminTurnToPending(commentId);
    }
}
