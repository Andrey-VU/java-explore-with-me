package ru.practicum.comments.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comments.dto.CommentDto;
import ru.practicum.comments.mapper.CommentMapper;
import ru.practicum.comments.service.CommentService;

@Validated
@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping(path = "admin/comments")
public class AdminCommentsController {
    CommentService commentService;
    CommentMapper commentMapper;

    @PatchMapping
    void adminModerateComment(@RequestBody CommentDto commentDto,
                              @RequestParam (defaultValue = "false") Boolean isToxic) {
        log.info("Comment for EVENT Id: {}, принято на модерацию", commentDto.getEventId());
        commentService.adminModerate(commentDto, isToxic);
    }

}
