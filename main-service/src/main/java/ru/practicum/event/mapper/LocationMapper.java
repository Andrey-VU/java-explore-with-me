package ru.practicum.event.mapper;

import org.mapstruct.Mapper;
import ru.practicum.event.model.Location;
import ru.practicum.event.dto.LocationDto;

@Mapper(componentModel = "spring")
public interface LocationMapper {
    Location makeLocation(LocationDto dto);
    LocationDto makeLocationDto(Location location);
}
