package ru.practicum.compilation.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.UpdateCompilationRequest;
import ru.practicum.compilation.mapper.CompilationMapper;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.compilation.repo.CompilationRepo;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.Event;
import ru.practicum.event.repo.EventRepo;
import ru.practicum.exception.NotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepo compilationRepo;
    private final CompilationMapper compilationMapper;
    private final EventRepo eventRepo;
    private final EventMapper eventMapper;

    @Override
    public CompilationDto create(NewCompilationDto newCompilationDto) {
        List<Event> events = makeListWithEvents(newCompilationDto.getEvents());
        Compilation compilation = compilationRepo.save(compilationMapper.makeEntity(newCompilationDto, events));
        List<EventShortDto> eventShortDtos = makeListWithEventShortDtos(events);
        CompilationDto compilationDto = compilationMapper.makeDto(compilation, eventShortDtos);
        log.info("Подборка событий СОЗДАНА И СОХРАНЕНА В БД: {}", compilationDto);
        return compilationDto;
    }

    @Override
    public void delete(Long compId) {
        Compilation compilation = compilationRepo.findById(compId)
            .orElseThrow(()
                -> new NotFoundException("Невозможно удалить отсутствующую в базе данных подборку событий Id" + compId));
        compilationRepo.delete(compilation);
        log.info("Подборка событий id {} УДАЛЕНА", compId);
    }

    @Override
    public List<CompilationDto> getAll(Boolean pinned, Integer from, Integer size) {
        PageRequest pageRequest = PageRequest.of(from > 0 ? from / size : 0, size);
        List<Compilation> compilations = compilationRepo.findAllByPinned(pinned, pageRequest);
        List<CompilationDto> compilationDtos = compilations.stream()
            .map(compilation
                -> compilationMapper.makeDto(compilation, makeListWithEventShortDtos(compilation.getEvents())))
            .collect(Collectors.toList());
        log.info("Найдено {} подборок событий подходящих под критерии: pinned = {}, from = {}, size = {}",
            compilationDtos.size(), pinned, from, size);

        return compilationDtos;
    }

    @Override
    public CompilationDto get(Long compId) {
        Compilation compilation = compilationRepo.findById(compId)
            .orElseThrow(() -> new NotFoundException("Не найдена подборка событий id" + compId));
        List<EventShortDto> events = makeListWithEventShortDtos(compilation.getEvents());
        CompilationDto compilationDto = compilationMapper.makeDto(compilation, events);
        log.info("По Id {} найдена подборка c названием \"{}\" из {} событий", compId,
                                                                               compilationDto.getTitle(), events.size());
        return compilationDto;
    }

    @Override
    public CompilationDto update(Long compId, UpdateCompilationRequest updateCompilationRequest) {
        Compilation compilation = compilationRepo.findById(compId)
            .orElseThrow(() -> new NotFoundException("Не найдена подборка событий id" + compId));
        Compilation updatedCompilation = prepareForUpdateCompilation(compilation, updateCompilationRequest);
        assert updatedCompilation != null;
        List<EventShortDto> events = makeListWithEventShortDtos(updatedCompilation.getEvents());
        CompilationDto compilationDto = compilationMapper.makeDto(updatedCompilation, events);
        log.info("ОБНОЫЛЕНА подборка событий: id = {}, название: \"{}\" из {} событий", compId,
            compilationDto.getTitle(), events.size());
        return compilationDto;
    }

    private Compilation prepareForUpdateCompilation(Compilation compilation, UpdateCompilationRequest updateCompilationRequest) {
        if (updateCompilationRequest.getPinned() != null)
            compilation.setPinned(updateCompilationRequest.getPinned());
        if (updateCompilationRequest.getEvents() != null) {
            List<Event> events = makeListWithEvents(updateCompilationRequest.getEvents());
            compilation.setEvents(events);
        }
        if (updateCompilationRequest.getTitle() != null || !updateCompilationRequest.getTitle().isBlank())
            compilation.setTitle(updateCompilationRequest.getTitle());
        return compilation;
    }

    private List<EventShortDto> makeListWithEventShortDtos(List<Event> events) {
        List<EventShortDto> eventShortDtos = new ArrayList<>();
        for (Event event : events) {
            eventShortDtos.add(eventMapper.makeShortDto(event));
        }
        return eventShortDtos;
    }

    private List<Event> makeListWithEvents(List<Long> events) {
        List<Event> eventsEntity = new ArrayList<>();
        events.forEach(eventId -> eventsEntity.add(eventRepo.findById(eventId)
            .orElseThrow(() -> new NotFoundException("Событие id " + eventId + " НЕ НАЙДЕНО"))));
        return eventsEntity;
    }

}
