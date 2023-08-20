package ru.practicum.event.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.model.Event;
import ru.practicum.user.model.User;

@Mapper(componentModel = "spring")
public interface EventMapper {
    @Mapping(target = "initiator", source = "initiator")
    @Mapping(target = "id", expression = "java(null)")
    Event makeEvent(NewEventDto newEventDto, User initiator);
    @Mapping(target = "views", source = "views")
    EventFullDto makeFullDto(Event event, Long views);
    EventShortDto makeShortDto(Event event);
}
