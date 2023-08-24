package ru.practicum.event.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import ru.practicum.category.model.Category;
import ru.practicum.event.enums.EventState;
import ru.practicum.event.model.Event;
import ru.practicum.event.repo.EventRepo;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class QueryHelper {
    EventRepo eventRepo;

    public List<Event> methodsDispatcher(List<Event> events, List<User> users, List<EventState> states,
                                         List<Category> categories, LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                         Integer from, Integer size) {

        PageRequest pageRequest = PageRequest.of(from > 0 ? from / size : 0, size);

        // 1 все параметры - null
        if (users == null && states == null && categories == null && rangeEnd == null && rangeStart == null) {
            events = eventRepo.findAll(pageRequest).stream().collect(Collectors.toList());
        }
        // 2, 3, 4, 5 users != null
        else if (users != null && states == null && categories == null && rangeEnd == null && rangeStart == null) {
            events = eventRepo.findAllByInitiatorIn(users, pageRequest);
        } else if (users != null && rangeStart != null && states == null && categories == null && rangeEnd == null) {
            events = eventRepo.findAllByInitiatorInAndEventDateAfter(users, rangeStart,
                pageRequest);
        } else if (users != null && rangeEnd != null && rangeStart == null && states == null && categories == null) {
            events = eventRepo.findAllByInitiatorInAndEventDateBefore(users, rangeEnd,
                pageRequest);
        } else if (users != null && rangeEnd != null && rangeStart != null && states == null && categories == null) {
            events = eventRepo.findAllByInitiatorInAndEventDateAfterAndEventDateBefore(users, rangeStart,
                rangeEnd, pageRequest);
        }
        // 6, 7, 8, 9 states != null
        else if (states != null && users == null && categories == null && rangeEnd == null && rangeStart == null) {
            events = eventRepo.findAllByStateIn(states, pageRequest);

        } else if (users == null && rangeStart != null && states != null && categories == null && rangeEnd == null) {
            events = eventRepo.findAllByStateInAndEventDateAfter(states, rangeStart, pageRequest);
        } else if (users == null && rangeEnd != null && rangeStart == null && states != null && categories == null) {
            events = eventRepo.findAllByStateInAndEventDateBefore(states, rangeEnd, pageRequest);
        } else if (users == null && rangeEnd != null && rangeStart != null && states != null && categories == null) {
            events = eventRepo.findAllByStateInAndEventDateAfterAndEventDateBefore(states, rangeStart, rangeEnd,
                pageRequest);
        }
        // 10, 11, 12, 13 categories != null
        else if (categories != null && states == null && users == null && rangeEnd == null && rangeStart == null) {
            events = eventRepo.findAllByCategoryIn(categories, pageRequest);

        } else if (categories != null && users == null && rangeStart != null && states == null && rangeEnd == null) {
            events = eventRepo.findAllByCategoryInAndEventDateAfter(categories, rangeStart, pageRequest);
        } else if (categories != null && users == null && rangeEnd != null && rangeStart == null && states == null) {
            events = eventRepo.findAllByCategoryInAndEventDateBefore(categories, rangeEnd, pageRequest);
        } else if (categories != null && rangeEnd != null && rangeStart != null && users == null  && states == null) {
            events = eventRepo.findAllByCategoryInAndEventDateAfterAndEventDateBefore(categories, rangeStart, rangeEnd,
                pageRequest);
        }

        // 14, 15, 16, 17  users & categories != null
        else if (categories != null && users != null && states == null && rangeEnd == null && rangeStart == null) {
            events = eventRepo.findAllByCategoryInAndInitiatorIn(categories, users, pageRequest);
        } else if (users != null && categories != null && rangeEnd != null && states == null && rangeStart == null) {
                events = eventRepo.findAllByCategoryInAndInitiatorInAndEventDateBefore(categories, users, rangeEnd,
                    pageRequest);
        } else if (users != null && categories != null && rangeStart != null && states == null && rangeEnd == null) {
            events = eventRepo.findAllByCategoryInAndInitiatorInAndEventDateAfter(categories, users, rangeStart,
                pageRequest);
        } else if (categories != null && users != null  && rangeEnd != null && rangeStart != null && states == null) {
            events = eventRepo.findAllByCategoryInAndInitiatorInAndEventDateAfterAndEventDateBefore(categories, users,
                rangeStart, rangeEnd, pageRequest);

        // 18, 19, 20, 21 users & states !=null
        } else if (states != null && users != null && categories == null && rangeEnd == null && rangeStart == null) {
            events = eventRepo.findAllByStateInAndInitiatorIn(states, users, pageRequest);

        } if (users != null && states != null  && categories == null && rangeEnd != null && rangeStart == null) {
            events = eventRepo.findAllByStateInAndInitiatorInAndEventDateBefore(states, users, rangeEnd,
                pageRequest);
        } else if (users != null && states != null && categories == null && rangeStart != null && rangeEnd == null) {
            events = eventRepo.findAllByStateInAndInitiatorInAndEventDateAfter(states, users, rangeStart,
                pageRequest);
        } else if (categories == null && users != null && rangeEnd != null && rangeStart != null && states != null) {
            events = eventRepo.findAllByStateInAndInitiatorInAndEventDateAfterAndEventDateBefore(states, users,
                rangeStart, rangeEnd, pageRequest);

        // 22, 23, 24, 25 users & states & categories !=null
        } else if (categories != null && states != null && users != null && rangeEnd == null && rangeStart == null) {
            events = eventRepo.findAllByStateInAndInitiatorInAndCategoryIn(states, users, categories, pageRequest);
        } else if (categories != null && states != null && users != null && rangeEnd != null && rangeStart == null) {
            events = eventRepo.findAllByStateInAndInitiatorInAndCategoryInAndEventDateBefore(states, users, categories,
                rangeEnd, pageRequest);
        }

        if (categories != null && states != null && users != null && rangeEnd == null && rangeStart != null) {
            events = eventRepo.findAllByStateInAndInitiatorInAndCategoryInAndEventDateAfter(states, users, categories,
                rangeStart, pageRequest);
        } else if (categories != null && states != null && users != null && rangeEnd != null && rangeStart != null) {
            events = eventRepo.findAllByStateInAndInitiatorInAndCategoryInAndEventDateAfterAndEventDateBefore(states,
                users, categories, rangeStart, rangeEnd, pageRequest);
        }

        // 26 end != null
        else if (users == null && states == null && categories == null && rangeStart == null && rangeEnd != null) {
            events = eventRepo.findAllByEventDateBefore(rangeEnd, pageRequest);
        }

        // 27 start != null
        else if (users == null && states == null && categories == null && rangeEnd == null && rangeStart != null) {
            events = eventRepo.findAllByEventDateAfter(rangeStart, pageRequest);

        // 28 end & start != null
        }

        if (users == null && states == null && categories == null && rangeEnd != null && rangeStart != null) {
            events = eventRepo.findAllByEventDateAfterAndEventDateBefore(rangeStart, rangeEnd, pageRequest);
        }

        return events;
    }
}
