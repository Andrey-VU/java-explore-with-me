package ru.practicum.comments.dto;

import lombok.Data;
import ru.practicum.comments.model.CommentState;
import ru.practicum.comments.model.Reaction;
import java.time.LocalDateTime;

@Data
public class CommentDto {
    private Long id;
    private Long eventId;
    private Long authorId;
    private String text;
    private Reaction reaction;
    private CommentState state;
    private String proposal;
    private LocalDateTime created;
    private Boolean isEdited;
}
