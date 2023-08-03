package ru.practicum.mapper;

import lombok.NoArgsConstructor;
import ru.practicum.dto.EndpointHit;
import ru.practicum.model.StatEntity;

@NoArgsConstructor
public final class StatManualMapper {

    public static StatEntity makeEntity(EndpointHit inputDto){
        StatEntity statEntity = new StatEntity();
        statEntity.setApp(inputDto.getApp());
        statEntity.setIp(inputDto.getIp());
        statEntity.setUri(inputDto.getUri());
        statEntity.setTimestamp(inputDto.getTimestamp());
        return statEntity;
    }

}

