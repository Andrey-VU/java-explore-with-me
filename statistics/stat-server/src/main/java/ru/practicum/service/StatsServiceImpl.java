package ru.practicum.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.dto.EndpointHit;
import ru.practicum.dto.ViewStats;
import ru.practicum.exception.ValidationException;
import ru.practicum.mapper.StatManualMapper;
import ru.practicum.repo.StatsRepo;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class StatsServiceImpl implements StatsService{
    private StatsRepo statsRepo;

    @Override
    public void addStats(EndpointHit inputDto) {
        statsRepo.save(StatManualMapper.makeEntity(inputDto));
    }

    @Override
    public List<ViewStats> viewStatistics(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        validateInterval(start, end);
        if (unique == false && uris.equals(null)) {
            return statsRepo.viewAllStatistics(start, end);
        } else return null;

    }

    private void validateInterval(LocalDateTime start, LocalDateTime end) {
        if (!start.isBefore(end)) {
            throw new ValidationException("Начало интервала должно быть раньше его завершения!");
        }
    }
}