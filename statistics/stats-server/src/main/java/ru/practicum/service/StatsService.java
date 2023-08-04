package ru.practicum.service;

import ru.practicum.dto.EndpointHit;
import ru.practicum.dto.ViewStats;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface StatsService {
    void addStats(EndpointHit inputDto);

    List<ViewStats> viewStatistics(LocalDateTime start, LocalDateTime end, Set<String> uris, boolean unique);
}
