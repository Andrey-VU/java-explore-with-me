package ru.practicum.event.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.category.model.Category;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.model.Event;
import ru.practicum.user.model.User;

@Mapper(componentModel = "spring")
public interface EventMapper {
    @Mapping(target = "initiator", source = "initiator")
    @Mapping(target = "id", expression = "java(null)")
    @Mapping(target = "category", source = "category")
    Event makeEvent(NewEventDto newEventDto, User initiator, Category category);
    @Mapping(target = "views", source = "views")
    @Mapping(target = "confirmedRequests", source = "participants")
    EventFullDto makeFullDtoAddViewsAndParticipants(Event event, Long views, Long participants);
    EventFullDto makeFullDto(Event event);
    EventShortDto makeShortDto(Event event);

    Event makeEventFromFullDto(EventFullDto fullDto);
}
