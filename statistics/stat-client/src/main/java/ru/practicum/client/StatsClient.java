package ru.practicum.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.dto.EndpointHit;
import ru.practicum.dto.ViewStats;
import ru.practicum.exception.EwmValidationException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@Service
public class StatsClient extends BaseClient {

    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Autowired
    public StatsClient(@Value("${stats-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
            builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build()
        );
    }

    public ResponseEntity<Object> addStats(EndpointHit inputDto) {
        log.info("StatsClient addStats: STARTED");
        ResponseEntity<Object> responseEntity = post("/hit", inputDto);
        return responseEntity;
    }

    public ResponseEntity<List<ViewStats>> viewStatistics(LocalDateTime start, LocalDateTime end, Set<String> uris) {
        return viewStatistics(start, end, uris, null);
    }

    public ResponseEntity<List<ViewStats>> viewStatistics(LocalDateTime start, LocalDateTime end) {
        return viewStatistics(start, end, null, null);
    }

    public ResponseEntity<List<ViewStats>> viewStatistics(LocalDateTime start, LocalDateTime end, Boolean unique) {
        return viewStatistics(start, end, null, unique);
    }

    public ResponseEntity<List<ViewStats>> viewStatistics(LocalDateTime start, LocalDateTime end,
                                                 Set<String> uris, Boolean unique) {

        validateInterval(start, end);

        StringBuilder uriBuilder = new StringBuilder("/stats" + "?start={start}&end={end}");
        Map<String, Object> parameters = Map.of(
            "start", start.format(DATE_TIME_FORMATTER),
            "end", end.format(DATE_TIME_FORMATTER)
        );

        if (uris != null && !uris.isEmpty()) {
            for (String uri : uris) {
                uriBuilder.append("&uris=").append(uri);
            }
        }
        if (unique != null) {
            uriBuilder.append("&unique=").append(unique);
        }

        return rest.exchange(uriBuilder.toString(), HttpMethod.GET, null, new ParameterizedTypeReference<>() {
        }, parameters);
    }
//  ResponseEntity<List<EndpointHitResponseDto>> response = rest.exchange(getStatPath, HttpMethod.GET, null,
//  new ParameterizedTypeReference<>() {
//  }, parameters);


    private void validateInterval(LocalDateTime start, LocalDateTime end) {
        if (!start.isBefore(end)) {
            throw new EwmValidationException("Начало интервала должно быть раньше его завершения!");
        }
    }
}