package ru.practicum.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.dto.EndpointHit;
import ru.practicum.dto.ViewStats;
import ru.practicum.mapper.StatsMapper;
import ru.practicum.repo.StatsRepo;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
@AllArgsConstructor
public class StatsServiceImpl implements StatsService {
    private StatsRepo statsRepo;
    private StatsMapper statsMapper;

    @Override
    public void addStats(EndpointHit inputDto) {
        statsRepo.save(statsMapper.makeEntity(inputDto));
    }

    @Override
    public List<ViewStats> viewStatistics(LocalDateTime start, LocalDateTime end, Set<String> uris, boolean unique) {
        if (unique) {
            if (uris == null || uris.isEmpty()) {
                return statsRepo.viewStatisticsUniqueIP(start, end);
            } else return statsRepo.viewStatisticsUniqueIPWithUris(start, end, uris);
        } else {
            if (uris == null || uris.isEmpty()) {
                return statsRepo.viewAllStatistics(start, end);
            } else {
                return statsRepo.viewAllStatisticsWithUris(start, end, uris);
            }
        }
    }
}