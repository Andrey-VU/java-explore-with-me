package ru.practicum.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.EndpointHit;
import ru.practicum.dto.ViewStats;
import ru.practicum.service.StatsService;

import javax.servlet.http.HttpServletRequest;

@Validated
@RestController
@Slf4j
public class StatsController {
    private StatsService statsService;
    public StatsController(StatsService statsService) {
        this.statsService = statsService;
    }

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public void addStats(@RequestBody EndpointHit inputDto) {
        log.info("Add new statistic for ip {} - Started", inputDto.getIp());
        statsService.addStats(inputDto);
    }

    @GetMapping("/stats")
    public ViewStats[] viewStatistics(@RequestParam String start,  //(в формате "yyyy-MM-dd HH:mm:ss")
                                      @RequestParam String end,
                                      @RequestParam(required = false) String[] uris,
                                      @RequestParam(name = "unique", defaultValue = "false") boolean unique){
        log.info("viewStatistics: STARTED");
        return statsService.viewStatistics(start, end, uris, unique);
    }

}
