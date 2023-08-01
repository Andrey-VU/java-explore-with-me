package ru.practicum;

import lombok.NoArgsConstructor;
import ru.practicum.model.StatEntity;

@NoArgsConstructor
public final class StatMapper{

    public static StatEntity makeEntity(EndpointHit inputDto){
        StatEntity statEntity = new StatEntity();

        statEntity.setApp(inputDto.getApp());
        statEntity.setIp(inputDto.getIp());
        statEntity.setUri(inputDto.getUri());
        statEntity.setTimestamp(inputDto.getTimestamp());

        return statEntity;
    }

    public static ViewStats makeDtoWithoutHits(StatEntity statEntity){
        ViewStats outputDto = new ViewStats();

        outputDto.setApp(statEntity.getApp());
        outputDto.setUri(statEntity.getUri());

        return outputDto;
    }
}
