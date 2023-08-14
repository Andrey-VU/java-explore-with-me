package ru.practicum.event.mapper;

import org.mapstruct.Mapper;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.model.Event;

@Mapper(componentModel = "spring")
public interface EventMapper {

    Event makeEvent(NewEventDto newEventDto);

}
