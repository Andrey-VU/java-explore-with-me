package ru.practicum.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.EndpointHit;
import ru.practicum.dto.ViewStats;
import ru.practicum.service.StatsServiceImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@RestController
@Slf4j
public class StatsController {
    private StatsServiceImpl statsServiceImpl;

    public StatsController(StatsServiceImpl statsServiceImpl) {
        this.statsServiceImpl = statsServiceImpl;
    }

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public void addStats(@RequestBody EndpointHit inputDto) {
        log.info("Add new statistic for ip {} - Started", inputDto.getIp());
        statsServiceImpl.addStats(inputDto);
    }

    @GetMapping("/stats")
    public List<ViewStats> viewStatistics(
        @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
        @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
        @RequestParam(required = false, name = "uris") Set<String> uris,
        @RequestParam(defaultValue = "false", name = "unique") boolean unique) {
        log.info("STARTs viewing statistics with parameters: \n start {}, \n end {}, \n uris {}, \n unique {}",
            start, end, uris, unique);
        return statsServiceImpl.viewStatistics(start, end, uris, unique);
    }
}
