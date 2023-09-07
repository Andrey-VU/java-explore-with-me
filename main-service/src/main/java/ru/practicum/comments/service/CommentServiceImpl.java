package ru.practicum.comments.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.comments.dto.CommentDto;
import ru.practicum.comments.dto.NewCommentDto;
import ru.practicum.comments.dto.query.QueryParamGetComments;
import ru.practicum.comments.mapper.CommentMapper;
import ru.practicum.comments.model.Comment;
import ru.practicum.comments.model.CommentState;
import ru.practicum.comments.model.Reaction;
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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

        return commentDto;
    }

    @Override
    public void adminModerate(CommentDto commentDto, Boolean isToxic) {
        // при установленной "токсичности" комментария
        // отменить его публикацию - выставить статус CANCELED
        // в других случаях - установить статус PUBLISHED

        Comment comment = commentRepo.findById(commentDto.getId())
            .orElseThrow(() -> new NotFoundException("Комментарий не найден"));
        if (!isToxic) {
            comment.setState(CommentState.PUBLISHED);
        } else comment.setState(CommentState.CANCELED);
        comment = commentRepo.save(comment);
        log.info("После модерации комментарию присвоен статус {}", comment.getState());
    }

    @Override
    public void privateDelete(Long commentId, Long userId) {
        isUserAnAuthor(userId, commentId);
        Comment comment = commentRepo.findById(commentId).orElseThrow(
            () -> new NotFoundException("Комментарий не найден"));
        commentRepo.delete(comment);
        log.info("Комментарий: {} успешно удалён по инициативе автора с Id {}",
            comment.getText(), userId);
    }

    @Override
    public CommentDto privateUpdate(Long userId, Long commentId, NewCommentDto newCommentDto) {
        Comment commentFromRepo = commentRepo.findById(commentId).orElseThrow(
            () -> new NotFoundException("Комментарий не найден"));
        isUserAnAuthor(userId, commentId);

        Comment updatedComment = makePrivateUpdate(commentFromRepo, newCommentDto);
        commentRepo.save(updatedComment);
        CommentDto commentDto = commentMapper.makeDto(updatedComment);
        commentDto.setIsEdited(true);
        log.info("Автор Id {} изменил свой комментарий: {}", userId, commentDto);

        // После обновления комментарий опять должен уйти на модерацию админу
        // на предмет токсичности

        return commentDto;
    }

    @Override
    public List<CommentDto> getComments(QueryParamGetComments queryParams, Integer from, Integer size) {
        List<Comment> commentsFromRepo = new ArrayList<>();
        LocalDateTime start = LocalDateTime.now().minusYears(100);
        LocalDateTime end = LocalDateTime.now().plusYears(100);
        PageRequest pageRequest = PageRequest.of(from > 0 ? from / size : 0, size);

        if (queryParams.getStart() != null) {
            start = queryParams.getStart();
        }
        if (queryParams.getEnd() != null) {
            end = queryParams.getEnd();
        }

        Reaction reaction = queryParams.getReaction();
        String text = queryParams.getText();
        Long eventId = queryParams.getEventId();

        if (text == null && reaction == null) {
            commentsFromRepo = commentRepo.findByEventIdAndCreatedAfterAndCreatedBefore
                (eventId, start, end, pageRequest);
        } else if (text != null && reaction != null) {
            commentsFromRepo
                = commentRepo.findByEventIdAndCreatedAfterAndCreatedBeforeAndReactionAndTextContainingIgnoreCase
                (eventId, start, end, reaction, text, pageRequest);
        }


        if (text == null && reaction != null) {
            commentsFromRepo = commentRepo.findByEventIdAndCreatedAfterAndCreatedBeforeAndReaction
                (eventId, start, end, reaction, pageRequest);
        } else if (text != null && reaction == null) {
            commentsFromRepo = commentRepo.findByEventIdAndCreatedAfterAndCreatedBeforeAndTextContainingIgnoreCase
                (eventId, start, end, text, pageRequest);
        }

        List<CommentDto> resultDtos = commentsFromRepo.stream()
            .map(comment -> commentMapper.makeDto(comment)).collect(Collectors.toList());

        log.info("Получена коллекция из {} комментариев", resultDtos.size());

        return resultDtos;
    }

    private void isUserAnAuthor(Long userId, Long commentId) {
        if (commentRepo.countByIdAndAuthorId(commentId, userId) == 0) {
            throw new NotFoundException("Пользователь id " + userId + "не оставлял комментарий " + commentId);
        }
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

    private Comment makePrivateUpdate(Comment commentFromRepo, NewCommentDto patch) {
        Comment commentAfterUpdate = new Comment();
        commentAfterUpdate.setId(commentFromRepo.getId());

        if (!patch.getProposal().isBlank() && patch.getProposal() != null) {
            commentAfterUpdate.setProposal(patch.getProposal());
        }
        if (patch.getReaction() != null) {
            commentAfterUpdate.setReaction(patch.getReaction());
        }
        if (!patch.getText().equals(commentFromRepo.getText())) {
            commentAfterUpdate.setText(patch.getText());
        }
        commentAfterUpdate.setState(CommentState.PENDING);

        return commentAfterUpdate;
    }


}
