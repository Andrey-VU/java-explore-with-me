package ru.practicum.mapper;

import org.mapstruct.Mapper;
import ru.practicum.EndpointHit;
import ru.practicum.ViewStats;
import ru.practicum.model.StatEntity;

@Deprecated
@Mapper(componentModel = "spring")
public interface StatMapper {
    StatEntity dtoToStatEntity(EndpointHit incomeDto);
    ViewStats statEntityToDto(StatEntity statEntity);
}
