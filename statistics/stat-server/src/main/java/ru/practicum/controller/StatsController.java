package ru.practicum.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.EndpointHit;
import ru.practicum.dto.ViewStats;
import ru.practicum.service.StatsServiceImpl;

import java.time.LocalDateTime;
import java.util.List;

@Validated
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
    public List<ViewStats> viewStatistics(@RequestParam LocalDateTime start,
                                          @RequestParam LocalDateTime end,
                                          @RequestParam(required = false) List<String> uris,
                                          @RequestParam(name = "unique", defaultValue = "false") boolean unique){
        log.info("STARTs viewing statistics with parameters: \n start {}, \n end {}, \n uris {}, \n unique {}",
                start, end, uris, unique );
        return statsServiceImpl.viewStatistics(start, end, uris, unique);
    }

}
