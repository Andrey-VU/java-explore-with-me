package ru.practicum.request.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import ru.practicum.request.enums.RequestState;

import java.time.LocalDateTime;

import static ru.practicum.utils.MainConstants.DATE_TIME_FORMAT;

@Data
public class ParticipationRequestDto {
    private Long id;

    @JsonFormat(pattern = DATE_TIME_FORMAT)
    private LocalDateTime created;
    private Long eventId;
    private Long requesterId;
    private RequestState requestState;
}
