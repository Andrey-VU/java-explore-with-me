package ru.practicum.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.event.enums.EventState;
import ru.practicum.event.model.Location;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

import static ru.practicum.utils.MainConstants.DATE_TIME_FORMAT;

@Data
public class NewEventDto {
    @NotBlank
    private String annotation;
    @NotNull
    private CategoryDto category;
    @NotBlank
    private String description;
    @NotNull
    @JsonFormat(pattern = DATE_TIME_FORMAT)
    private LocalDateTime eventDate;
    @NotNull
    private Location location;
    private Boolean paid;
    private Integer participantLimit;
    private Boolean requestModeration;
    @NotBlank
    private String title;
    private EventState eventState = EventState.PENDING;
}