package ru.practicum.event.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.client.StatsClient;
import ru.practicum.dto.EndpointHit;
import ru.practicum.dto.ViewStats;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class ViewServiceImpl implements ViewService{
    private static final String APP = "ewm-main-service";

    private final StatsClient statsClient;

    @Override
    public Map<Long, Long> getViews(List<Long> eventIds) {
        return null;
    }

    @Override
    public Long getViewsById(Long eventId) {
        Set<String> uris = new HashSet<>();
        uris.add("/events" + "/" + eventId);
        ResponseEntity<List<ViewStats>> views = statsClient.viewStatistics(
            LocalDateTime.now().minusYears(100),
            LocalDateTime.now().plusYears(100),
            uris,
            true);
        if (!views.getBody().isEmpty() && views.getBody() != null ) {
            ViewStats body = views.getBody().get(0);
            return body.getHits();
        } else return 0L;

    }

    @Override
    public ResponseEntity<Object> saveHit(String uri, String ip) {
        EndpointHit dto = EndpointHit.builder()
            .ip(ip)
            .uri(uri)
            .timestamp(LocalDateTime.now())
            .app(APP)
            .build();

        return statsClient.addStats(dto);
    }
}
