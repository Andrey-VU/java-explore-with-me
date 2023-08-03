package ru.practicum.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.dto.EndpointHit;
import ru.practicum.dto.ViewStats;
import ru.practicum.mapper.StatManualMapper;
import ru.practicum.repo.StatsRepo;

@Service
@AllArgsConstructor
public class StatsService {
    private StatsRepo statsRepo;

    public void addStats(EndpointHit inputDto) {
        statsRepo.save(StatManualMapper.makeEntity(inputDto));
    }

    public ViewStats[] viewStatistics(String start, String end, String[] uris, boolean unique) {
        return new ViewStats[0];
    }
}
//    [
//    {
//        "app": "ewm-main-service",
//            "uri": "/events/1",
//            "hits": 6
//    }
//]

