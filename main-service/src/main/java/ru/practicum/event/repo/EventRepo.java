package ru.practicum.event.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.event.model.Event;

public interface EventRepo extends JpaRepository<Event, Long> {
}
