package ru.practicum.event.service;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.category.model.Category;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.UpdateEventAdminRequest;
import ru.practicum.event.enums.EventState;
import ru.practicum.event.enums.SortBy;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.mapper.LocationMapper;
import ru.practicum.event.model.Event;
import ru.practicum.event.repo.EventRepo;
import ru.practicum.exception.NotFoundException;
import ru.practicum.user.model.User;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class EventServiceImpl implements EventService{
    private final EventRepo eventRepo;
    private final ViewService viewService;
    private final EventMapper eventMapper;
    //private final LocationMapper locationMapper;
    //private final

    @Override
    public List<EventFullDto> getAdmin(List<User> users, List<EventState> states, List<Category> categories,
                                       LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from, Integer size) {
        //Эндпоинт возвращает полную информацию обо всех событиях подходящих под переданные условия
        //В случае, если по заданным фильтрам не найдено ни одного события, возвращает пустой список
        List<EventFullDto> eventsForAdmin = new ArrayList<>();
        PageRequest pageRequest = PageRequest.of(from > 0 ? from / size : 0, size);

        return eventsForAdmin;
    }

    @Override
    public EventFullDto updateAdmin(Long eventId, UpdateEventAdminRequest updateRequestDto) {
        return null;
    }

    @Override
    public List<EventFullDto> getPublicEvents(String text, Boolean paid, List<Category> categories,
                                              LocalDateTime rangeStart, LocalDateTime rangeEnd, Boolean onlyAvailable,
                                              SortBy sort, Integer from, Integer size) {
        return null;
    }

    @Override
    public EventFullDto getPublic(Long id, HttpServletRequest request) {
        Event event = eventRepo.findByIdAndPublishedOnBefore(id, LocalDateTime.now())
            .orElseThrow(() -> new NotFoundException("Событие id " + id + " - не найдено"));
        log.info("Найдено событие {}", event);

        return makeDtoWithViews(event, request);
    }

    private EventFullDto makeDtoWithViews(Event event, HttpServletRequest request) {
        Long views = viewService.getViewsById(event.getId(), request);
        log.info("Событие {} было просмотрено {} раз", event, views);
        EventFullDto fullDto = eventMapper.makeFullDto(event);
        fullDto.setViews(views);
        return fullDto;
    }
}
