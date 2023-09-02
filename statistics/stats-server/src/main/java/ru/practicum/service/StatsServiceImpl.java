package ru.practicum.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.EndpointHit;
import ru.practicum.dto.ViewStats;
import ru.practicum.exceptions.EwmValidationException;
import ru.practicum.mapper.StatsMapper;
import ru.practicum.model.StatEntity;
import ru.practicum.repo.StatsRepo;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
@AllArgsConstructor
public class StatsServiceImpl implements StatsService {
    private StatsRepo statsRepo;
    private StatsMapper statsMapper;

    @Override
    @Transactional
    public void addStats(EndpointHit inputDto) {
        StatEntity entity = statsRepo.save(statsMapper.makeEntity(inputDto));
        log.info("Статистика СОХРАНЕНА:{}", entity);
    }

    @Override
    public List<ViewStats> viewStatistics(LocalDateTime start, LocalDateTime end, Set<String> uris, boolean unique) {

        if (!start.isBefore(end)) {
            log.warn("Введено некорректное время начала или конца интервала");
            throw new EwmValidationException("Введён некорректный временной интервал");
        }

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