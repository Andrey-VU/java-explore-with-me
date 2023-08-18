package ru.practicum.event.service;

import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

public interface ViewService {
    Map<Long, Long> getViews(List<Long> eventIds);
    Long getViewsById(Long eventId, HttpServletRequest request);
    ResponseEntity<Object> saveHit(String uri, String ip);
}
