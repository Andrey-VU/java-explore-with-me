package ru.practicum.comments.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.comments.dto.CommentDto;
import ru.practicum.comments.dto.NewCommentDto;
import ru.practicum.comments.mapper.CommentMapper;
import ru.practicum.comments.model.Comment;
import ru.practicum.comments.model.CommentState;
import ru.practicum.comments.repo.CommentRepo;
import ru.practicum.event.model.Event;
import ru.practicum.event.repo.EventRepo;
import ru.practicum.exception.EwmConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.request.enums.RequestState;
import ru.practicum.request.repo.RequestRepo;
import ru.practicum.user.model.User;
import ru.practicum.user.repo.UserRepo;

import java.time.LocalDateTime;

@Service
@Slf4j
@AllArgsConstructor
public class CommentServiceImpl implements CommentService{
    private final CommentRepo commentRepo;
    private final UserRepo userRepo;
    private final EventRepo eventRepo;
    private final CommentMapper commentMapper;
    private final RequestRepo requestRepo;

    @Override
    public CommentDto create(Long userId, Long eventId, NewCommentDto newCommentDto) {
        // проверить участвовал ли юзер в событии - 409
        // оставить отзыв можно не раньше чем через 15 минут от начала события, но не позже 5 дней - 409
        // инициатор не может писать отзыв на своё событие - 409

        User author = userRepo.findById(userId).orElseThrow(()
            -> new NotFoundException("Пользователь не найден. Id " + userId));
        Event event = eventRepo.findById(eventId).orElseThrow(()
            -> new NotFoundException("Событие не найдено. Id " + eventId));

        isCommentPossible(author, event);

        Comment comment = commentMapper.makeComment(newCommentDto, author, event, LocalDateTime.now());
        comment = commentRepo.save(comment);
        CommentDto commentDto = commentMapper.makeDto(comment);

        log.info("Комментарий {} отправлен на модерацию", commentDto);

        return null;
    }

    @Override
    public void adminModerate(CommentDto commentDto) {
        Comment comment = commentRepo.findById(commentDto.getId())
            .orElseThrow(() -> new NotFoundException("Комментарий не найден"));
        if (!comment.getIsToxic()) {
            comment.setState(CommentState.PUBLISHED);
        } else comment.setState(CommentState.CANCELED);
        comment = commentRepo.save(comment);
        log.info("После модерации комментарию присвоен статус {}", comment.getState());
    }

    @Override
    public void delete(Long commentId) {

    }

    @Override
    public CommentDto update(Long userId, Long commentId, NewCommentDto newCommentDto) {
        return null;
    }

    @Override
    public void adminTurnToPending(Long commentId) {

    }

    private void isCommentPossible(User author, Event event) {
        if (event.getInitiator().equals(author))
            throw new EwmConflictException("Инициатор не может писать отзывы на свои события");
        if (event.getEventDate().isAfter(LocalDateTime.now().plusMinutes(15)) ||
            event.getEventDate().isBefore(LocalDateTime.now().minusDays(5)))
            throw new EwmConflictException("Нельзя оставить отзыв раньше чем через 15 минут и позже чем через" +
                " 5 дней от начала события ");
        if (requestRepo.findAllByEventId(event.getId()).stream()
            .filter(request -> request.getRequester().getId().equals(author.getId()))
            .filter(request -> request.getStatus().equals(RequestState.CONFIRMED))
            .count() == 0)
            throw new EwmConflictException("Нельзя написать отзыв если вы не участвовали в событии");
    }


}
