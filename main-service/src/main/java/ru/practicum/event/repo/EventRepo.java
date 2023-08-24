package ru.practicum.event.repo;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.category.model.Category;
import ru.practicum.event.enums.EventState;
import ru.practicum.event.enums.SortBy;
import ru.practicum.event.model.Event;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EventRepo extends JpaRepository<Event, Long>, QuerydslPredicateExecutor<Event> {
    Optional<Event> findByIdAndPublishedOnBefore(Long id, LocalDateTime now);

    List<Event> findAllByInitiatorId(Long userId, PageRequest pageRequest);

    List<Event> findAllByEventDateBefore(LocalDateTime rangeEnd, PageRequest pageRequest);

    List<Event> findAllByEventDateAfter(LocalDateTime rangeStart, PageRequest pageRequest);

    List<Event> findAllByEventDateAfterAndEventDateBefore(LocalDateTime rangeStart,
                                                          LocalDateTime rangeEnd,
                                                          PageRequest pageRequest);

    List<Event> findAllByInitiatorIn(List<User> users, PageRequest pageRequest);

    List<Event> findAllByCategoryIn(List<Category> categories, PageRequest pageRequest);

    List<Event> findAllByStateIn(List<EventState> states, PageRequest pageRequest);

    List<Event> findAllByCategoryInAndInitiatorIn(List<Category> categories, List<User> users, PageRequest pageRequest);

    List<Event> findAllByStateInAndInitiatorIn(List<EventState> states, List<User> users, PageRequest pageRequest);

    List<Event> findAllByStateInAndInitiatorInAndCategoryInAndEventDateBefore(List<EventState> states, List<User> users,
                                                                              List<Category> categories,
                                                                              LocalDateTime rangeEnd, PageRequest pageRequest);

    List<Event> findAllByInitiatorInAndEventDateAfterAndEventDateBefore(List<User> users, LocalDateTime rangeStart, 
                                                                        LocalDateTime rangeEnd, PageRequest pageRequest);

    List<Event> findAllByCategoryInAndInitiatorInAndEventDateAfterAndEventDateBefore(List<Category> categories,
                                                                                     List<User> users, 
                                                                                     LocalDateTime rangeStart, 
                                                                                     LocalDateTime rangeEnd,
                                                                                     PageRequest pageRequest);

    List<Event> findAllByInitiatorInAndEventDateAfter(List<User> users, LocalDateTime rangeStart, PageRequest pageRequest);

    List<Event> findAllByInitiatorInAndEventDateBefore(List<User> users, LocalDateTime rangeEnd, PageRequest pageRequest);

    List<Event> findAllByStateInAndEventDateAfterAndEventDateBefore(List<EventState> states, LocalDateTime rangeStart, LocalDateTime rangeEnd, PageRequest pageRequest);

    List<Event> findAllByStateInAndEventDateAfter(List<EventState> states, LocalDateTime rangeStart, PageRequest pageRequest);

    List<Event> findAllByStateInAndEventDateBefore(List<EventState> states, LocalDateTime rangeEnd, PageRequest pageRequest);

    List<Event> findAllByCategoryInAndEventDateAfter(List<Category> categories, LocalDateTime rangeStart, PageRequest pageRequest);

    List<Event> findAllByCategoryInAndEventDateBefore(List<Category> categories, LocalDateTime rangeEnd, PageRequest pageRequest);

    List<Event> findAllByCategoryInAndEventDateAfterAndEventDateBefore(List<Category> categories, LocalDateTime rangeStart, LocalDateTime rangeEnd, PageRequest pageRequest);

    List<Event> findAllByCategoryInAndInitiatorInAndEventDateBefore(List<Category> categories, List<User> users, LocalDateTime rangeEnd, PageRequest pageRequest);

    List<Event> findAllByCategoryInAndInitiatorInAndEventDateAfter(List<Category> categories, List<User> users, LocalDateTime rangeStart, PageRequest pageRequest);

    List<Event> findAllByStateInAndInitiatorInAndEventDateBefore(List<EventState> states, List<User> users, LocalDateTime rangeEnd, PageRequest pageRequest);

    List<Event> findAllByStateInAndInitiatorInAndEventDateAfter(List<EventState> states, List<User> users, LocalDateTime rangeStart, PageRequest pageRequest);

    List<Event> findAllByStateInAndInitiatorInAndEventDateAfterAndEventDateBefore(List<EventState> states, List<User> users, LocalDateTime rangeStart, LocalDateTime rangeEnd, PageRequest pageRequest);

    List<Event> findAllByStateInAndInitiatorInAndCategoryIn(List<EventState> states, List<User> users, List<Category> categories, PageRequest pageRequest);

    List<Event> findAllByStateInAndInitiatorInAndCategoryInAndEventDateAfter(List<EventState> states, List<User> users, List<Category> categories, LocalDateTime rangeStart, PageRequest pageRequest);

    List<Event> findAllByStateInAndInitiatorInAndCategoryInAndEventDateAfterAndEventDateBefore(List<EventState> states, List<User> users, List<Category> categories,
                                                                                               LocalDateTime rangeStart, LocalDateTime rangeEnd, PageRequest pageRequest);

//    @Query("select e from Event e where e.isAvailable = True AND " +
//        "( upper(i.name) like upper(concat('%', ?1, '%')) " +
//        " or upper(i.description) like upper(concat('%', ?1, '%')))")
    List<Event> getEventsPublic(String text,
                                Boolean paid,
                                List<Category> categories,
                                LocalDateTime rangeStart,
                                LocalDateTime rangeEnd,
                                Boolean onlyAvailable,
                                SortBy sort,
                                Integer from,
                                Integer size);

//    List<Event> getEventsAdmin(List<User> users,
//                               List<EventState> states,
//                               List<Category> categories,
//                               LocalDateTime rangeStart,
//                               LocalDateTime rangeEnd,
//                               Integer from,
//                               Integer size);
}
