package ru.practicum.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.dto.ViewStats;
import ru.practicum.model.StatEntity;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsRepo extends JpaRepository<StatEntity, Long> {

    @Query("SELECT new ru.practicum.dto.ViewStats(s.app, s.uri, COUNT(s.ip)) " +
            "FROM StatEntity AS s " +
            "WHERE s.timestamp BETWEEN ?1 AND ?2 " +
            "GROUP BY s.app, s.uri " +
            "ORDER BY COUNT(s.ip) DESC")
    List<ViewStats> viewAllStatistics(LocalDateTime start, LocalDateTime end);
//
//    @Query()
//    List<StatEntity> viewStatisticsUniqueIP(LocalDateTime start, LocalDateTime end, List<String> uris);
}
