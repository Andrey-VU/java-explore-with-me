package ru.practicum.comments.service;

import ru.practicum.comments.dto.CommentDto;
import ru.practicum.comments.dto.NewCommentDto;
import ru.practicum.comments.dto.query.QueryParamGetComments;

import java.util.List;

public interface CommentService {
    CommentDto create(Long userId, Long eventId, NewCommentDto newCommentDto);

    void adminModerate(CommentDto commentDto, Boolean isToxic);

    void privateDelete(Long commentId, Long userId);

    CommentDto privateUpdate(Long userId, Long commentId, NewCommentDto newCommentDto);

    List<CommentDto> getComments(QueryParamGetComments queryParams, Integer from, Integer size);

}
