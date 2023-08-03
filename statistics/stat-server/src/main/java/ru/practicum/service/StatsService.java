package ru.practicum.service;

import org.springframework.stereotype.Service;
import ru.practicum.EndpointHit;
import ru.practicum.ViewStats;

@Service
public class StatsService {
    public void addStats(EndpointHit inputDto) {

//{
        //  "app": "ewm-main-service",
        //  "uri": "/events/1",
        //  "ip": "192.163.0.1",
        //  "timestamp": "2022-09-06 11:00:23"
        //}

    }

    public ViewStats[] viewStatistics(String start, String end, String[] uris, boolean unique) {
        return new ViewStats[0];
    }
}


//    [
//    {
//        "app": "ewm-main-service",
//            "uri": "/events/1",
//            "hits": 6
//    }
//]

