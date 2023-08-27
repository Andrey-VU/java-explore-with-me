package ru.practicum.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import ru.practicum.event.enums.EventState;
import ru.practicum.event.model.Location;

import javax.validation.constraints.*;
import java.time.LocalDateTime;

import static ru.practicum.utils.MainConstants.DATE_TIME_FORMAT;

@Data
public class NewEventDto {
    @NotBlank
    @Size(min = 20, max = 2000)
    private String annotation;

    @Positive
    private Long category;

    @NotBlank
    @Size(min = 20, max = 7000)
    private String description;

    @NotNull
    @JsonFormat(pattern = DATE_TIME_FORMAT)
    private LocalDateTime eventDate;
    @NotNull
    private Location location;
    private Boolean paid;
    @PositiveOrZero
    private Integer participantLimit;
    private Boolean requestModeration;
    @NotBlank
    @Size(min = 3, max = 120)
    private String title;
    private EventState state = EventState.PENDING;
}