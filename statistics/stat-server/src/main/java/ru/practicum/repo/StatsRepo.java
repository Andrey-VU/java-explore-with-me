package ru.practicum.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.model.StatEntity;

public interface StatsRepo extends JpaRepository<StatEntity, Long> {
}
