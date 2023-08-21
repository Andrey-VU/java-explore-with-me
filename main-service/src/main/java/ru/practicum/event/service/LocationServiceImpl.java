package ru.practicum.event.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.event.model.Location;
import ru.practicum.event.repo.LocationRepo;

@Service
@AllArgsConstructor
public class LocationServiceImpl implements LocationService{
    private final LocationRepo locationRepo;

    @Override
    public Location save(Location location) {
        return locationRepo.save(location);
    }
}
