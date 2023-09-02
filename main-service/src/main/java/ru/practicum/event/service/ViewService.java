package ru.practicum.event.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface ViewService {
    Long getViewsById(Long eventId);

    ResponseEntity<Object> saveHit(String uri, String ip);
}
