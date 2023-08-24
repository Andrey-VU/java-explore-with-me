package ru.practicum.request.mapper;

import org.mapstruct.Mapper;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.model.Request;

@Mapper(componentModel = "spring")
public interface RequestMapper {
    Request makeEntity(ParticipationRequestDto dto);
    ParticipationRequestDto makeRequestDto(Request request);
}