package ru.practicum.comments.dto.query;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.comments.model.Reaction;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

import static ru.practicum.utils.MainConstants.DATE_TIME_FORMAT;

@Data
public class QueryParamGetComments {
    @NotNull(message = "Необходимо указать Id события для поиска комментариев")
    private Long eventId;
    private Reaction reaction;

    @DateTimeFormat(pattern = DATE_TIME_FORMAT)
    private LocalDateTime start;
    @DateTimeFormat(pattern = DATE_TIME_FORMAT)
    private LocalDateTime end;

    private String text;
}
