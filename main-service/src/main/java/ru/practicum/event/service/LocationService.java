package ru.practicum.event.service;

import org.springframework.stereotype.Service;
import ru.practicum.event.model.Location;

@Service
public interface LocationService {
    Location save(Location location);
}
