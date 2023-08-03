package ru.practicum.service;

import ru.practicum.dto.EndpointHit;
import ru.practicum.dto.ViewStats;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsService {
    void addStats(EndpointHit inputDto);
    List<ViewStats> viewStatistics(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique);
}
