package ru.practicum.event.repo;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.event.model.Event;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EventRepo extends JpaRepository<Event, Long> {
    Optional<Event> findByIdAndPublishedOnBefore(Long id, LocalDateTime now);

    List<Event> findAllByInitiatorId(Long userId, PageRequest pageRequest);
}
