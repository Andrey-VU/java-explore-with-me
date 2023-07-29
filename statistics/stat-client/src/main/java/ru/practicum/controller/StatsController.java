package ru.practicum.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.service.StatsService;

@RestController
@RequestMapping(path = "/hit")
@Slf4j
public class StatsController {
    private StatsService statsService;
    public StatsController(StatsService statsService) {
        this.statsService = statsService;
    }


    /*
    /hit:
    post:
      tags:
        - StatsController
      summary: Сохранение информации о том, что к эндпоинту был запрос
      description: >-
        Сохранение информации о том, что на uri конкретного сервиса был
        отправлен запрос пользователем. Название сервиса, uri и ip пользователя
        указаны в теле запроса.
      operationId: hit
      requestBody:
        description: данные запроса
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/EndpointHit'
        required: true
      responses:
        '201':
          description: Информация сохранена

     */


}
