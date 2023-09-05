package ru.practicum.comments.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.comments.dto.CommentDto;
import ru.practicum.comments.dto.NewCommentDto;
import ru.practicum.comments.repo.CommentRepo;
import ru.practicum.user.repo.UserRepo;

@Service
@Slf4j
@AllArgsConstructor
public class CommentServiceImpl implements CommentService{
    private final CommentRepo commentRepo;
    private final UserRepo userRepo;

    @Override
    public CommentDto create(Long userId, Long eventId, NewCommentDto newCommentDto) {
        // проверить участвовал ли юзер в событии
        // оставить отзыв можно только если событие уже прошло




        return null;
    }
}
