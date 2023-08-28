package ru.practicum.compilation.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.UpdateCompilationRequest;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.model.Event;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CompilationMapper {
    @Mapping(target = "events", source = "events")
    Compilation makeEntity(NewCompilationDto newCompilationDto, List<Event> events);
    @Mapping(target = "events", source = "events")
    CompilationDto makeDto(Compilation compilation, List<EventShortDto> events);
    @Mapping(target = "events", source = "events")
    Compilation updateEntity(UpdateCompilationRequest updateCompilationRequest, List<Event> events);
}
