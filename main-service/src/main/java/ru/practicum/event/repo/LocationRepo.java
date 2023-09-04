package ru.practicum.event.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.event.model.Location;

public interface LocationRepo extends JpaRepository<Location, Long> {
}
