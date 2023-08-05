package ru.practicum.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.dto.EndpointHitClient;
import ru.practicum.exception.ValidationException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Set;

@Slf4j
public class StatsClient extends BaseClient {

    @Autowired
    public StatsClient(@Value("${stats-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
            builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build()
        );
    }

    public void addStats(EndpointHitClient inputDto) {
        log.info("StatsClient addStats: STARTED");
        post("/hit", inputDto);
    }

    public ResponseEntity<Object> viewStatistics(LocalDateTime start, LocalDateTime end, Set<String> uris) {
        return viewStatistics(start, end, uris, null);
    }

    public ResponseEntity<Object> viewStatistics(LocalDateTime start, LocalDateTime end) {
        return viewStatistics(start, end, null, null);
    }

    public ResponseEntity<Object> viewStatistics(LocalDateTime start, LocalDateTime end, Boolean unique) {
        return viewStatistics(start, end, null, unique);
    }

    public ResponseEntity<Object> viewStatistics(LocalDateTime start, LocalDateTime end,
                                                 Set<String> uris, Boolean unique) {
        validateInterval(start, end);

        StringBuilder uriBuilder = new StringBuilder("/stats" + "?start={start}&end={end}");
        Map<String, Object> parameters = Map.of(
            "start", start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
            "end", end.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        );

        if (uris != null && !uris.isEmpty()) {
            for (String uri : uris) {
                uriBuilder.append("&uris=").append(uri);
            }
        }
        if (unique != null) {
            uriBuilder.append("&unique=").append(unique);
        }

        return get(uriBuilder.toString(), parameters);
    }

    private void validateInterval(LocalDateTime start, LocalDateTime end) {
        if (!start.isBefore(end)) {
            throw new ValidationException("Начало интервала должно быть раньше его завершения!");
        }
    }
}