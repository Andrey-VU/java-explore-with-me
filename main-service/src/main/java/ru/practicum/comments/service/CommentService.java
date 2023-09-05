package ru.practicum.comments.service;

import org.springframework.stereotype.Service;
import ru.practicum.comments.dto.CommentDto;
import ru.practicum.comments.dto.NewCommentDto;

public interface CommentService {
    CommentDto create(Long userId, Long eventId, NewCommentDto newCommentDto);
}
