package ru.practicum.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.EndpointHit;
import ru.practicum.ViewStats;
import ru.practicum.service.StatsService;

import javax.validation.Valid;

@Validated
@RestController
@RequestMapping(path = "")
@Slf4j
public class StatsController {
    private StatsService statsService;
    public StatsController(StatsService statsService) {
        this.statsService = statsService;
    }

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public void addStats(@Valid @RequestBody EndpointHit inputDto) {   // или валидация в мейн сервисе?
        log.info("Add new statistic for ip  - Started", inputDto.getIp());
        statsService.addStats(inputDto);
    }

    @GetMapping("/stats")
    public ViewStats[] viewStatistics(@RequestParam String start,  //(в формате "yyyy-MM-dd HH:mm:ss")
                                      @RequestParam String end,
                                      @RequestParam(required = false) String[] uris,
                                      @RequestParam(name = "unique", defaultValue = "false") boolean unique){
        return statsService.viewStatistics(start, end, uris, unique);
    }

}
