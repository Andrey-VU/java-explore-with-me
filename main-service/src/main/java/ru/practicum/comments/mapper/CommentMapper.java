package ru.practicum.comments.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.comments.dto.NewCommentDto;
import ru.practicum.comments.model.Comment;
import ru.practicum.event.model.Event;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring")
public interface CommentMapper {
    @Mapping(target = "create", source = "created")
    @Mapping(target = "event", source = "event")
    @Mapping(target = "author", source = "user")
    Comment makeComment(NewCommentDto newCommentDto, User user, Event event, LocalDateTime created);
}
