package ru.practicum.event.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface ViewService {
    Long getViewsById(Long eventId);
    ResponseEntity<Object> saveHit(String uri, String ip);
}
