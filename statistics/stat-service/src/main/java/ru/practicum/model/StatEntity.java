package ru.practicum.model;

public class StatEntity {
    private int id;  // сделать доступным только для чтения // "readOnly": true,
    private String app;
    private String uri;
    private String ip;
    private String timestamp;
    private int hits;
}
