package ru.practicum.request.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.request.model.Request;

public interface RequestRepo extends JpaRepository<Request, Long> {
}
