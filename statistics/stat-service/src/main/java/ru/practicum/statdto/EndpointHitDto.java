package ru.practicum.statdto;

public class EndpointHitDto {  // inputDto
    private int id;  // сделать доступным только для чтения // "readOnly": true,
    private String app;
    private String uri;
    private String ip;
    private String timestamp;

}
