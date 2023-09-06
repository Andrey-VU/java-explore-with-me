package ru.practicum.comments.service;

import org.springframework.stereotype.Service;
import ru.practicum.comments.dto.CommentDto;
import ru.practicum.comments.dto.NewCommentDto;
import ru.practicum.comments.model.Comment;

public interface CommentService {
    CommentDto create(Long userId, Long eventId, NewCommentDto newCommentDto);

    void adminModerate(CommentDto commentDto);

    void delete(Long commentId);

    CommentDto update(Long userId, Long commentId, NewCommentDto newCommentDto);

    void adminTurnToPending(Long commentId);
}
