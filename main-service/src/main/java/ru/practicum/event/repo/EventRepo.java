package ru.practicum.event.repo;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.event.enums.EventState;
import ru.practicum.event.model.Event;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EventRepo extends JpaRepository<Event, Long> {
    Optional<Event> findByIdAndPublishedOnBefore(Long id, LocalDateTime now);

    List<Event> findAllByInitiatorId(Long userId, PageRequest pageRequest);

//    @Query("SELECT e FROM Event e " +
//        "WHERE (e.initiator.id IN (:users) OR :users IS NULL) " +
//        "AND (e.state IN (:states) OR :states IS NULL) " +
//        "AND (e.category.id IN (:categories) OR :categories IS NULL) " +
//        "AND (e.eventDate BETWEEN :rangeStart AND :rangeEnd) " +
//        "ORDER BY e.createdOn DESC")
//    Page<Event> getEventsAdmin(List<Long> users, List<EventState> states, List<Long> categories,
//                               LocalDateTime rangeStart, LocalDateTime rangeEnd, Pageable pageable);

    @Query("SELECT e FROM Event e " +
        "WHERE ((LOWER(e.annotation) LIKE CONCAT('%',lower(:text),'%') " +
        "OR LOWER(e.description) LIKE CONCAT('%',lower(:text),'%')) " +
        "OR :text IS NULL ) " +
        "AND (e.category.id IN (:categories) OR :categories IS NULL) " +
        "AND (e.paid IN (:paid) OR :paid IS NULL) " +
        "AND (e.eventDate BETWEEN :rangeStart AND :rangeEnd)")
    List<Event> getEventsPublic(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart,
                                LocalDateTime rangeEnd, Pageable pageable);

    List<Event> findAllByEventDateAfterAndEventDateBefore(LocalDateTime rangeStart, LocalDateTime rangeEnd);

    List<Event> findAllByEventDateAfterAndEventDateBeforeAndStateIn(LocalDateTime rangeStart,
                                                                    LocalDateTime rangeEnd, List<EventState> states,
                                                                    PageRequest pageRequest);

    List<Event> findAllByEventDateAfterAndEventDateBeforeAndStateInAndInitiatorIdIn(LocalDateTime rangeStart,
                                                                                    LocalDateTime rangeEnd,
                                                                                    List<EventState> states,
                                                                                    List<Long> usersIds,
                                                                                    PageRequest pageRequest);

    List<Event> findAllByEventDateAfterAndEventDateBeforeAndStateInAndInitiatorIdInAndCategoryIdIn(
        LocalDateTime rangeStart,
        LocalDateTime rangeEnd, List<EventState> states, List<Long> usersIds, List<Long> categoriesIds,
        PageRequest pageRequest);

    List<Event> findAllByEventDateAfterAndEventDateBeforeAndStateInAndCategoryIdIn(LocalDateTime rangeStart,
                                                                                   LocalDateTime rangeEnd,
                                                                                   List<EventState> states,
                                                                                   List<Long> categoriesIds,
                                                                                   PageRequest pageRequest);
}
