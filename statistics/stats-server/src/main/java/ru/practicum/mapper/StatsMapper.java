package ru.practicum.mapper;

import org.mapstruct.Mapper;
import ru.practicum.dto.EndpointHit;
import ru.practicum.model.StatEntity;

@Mapper(componentModel = "spring")
public interface StatsMapper {
    StatEntity makeEntity(EndpointHit inputDto);
}
