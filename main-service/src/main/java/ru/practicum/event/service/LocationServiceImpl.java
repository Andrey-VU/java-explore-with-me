package ru.practicum.event.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.event.model.Location;
import ru.practicum.event.repo.LocationRepo;

@Service
@AllArgsConstructor
public class LocationServiceImpl implements LocationService {
    private final LocationRepo locationRepo;

    @Override
    @Transactional
    public Location save(Location location) {
        return locationRepo.save(location);
    }
}
