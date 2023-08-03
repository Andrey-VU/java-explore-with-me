package ru.practicum.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
@EqualsAndHashCode
public class ViewStatsClient {
    private String app;
    private String uri;
    private int hits;
}
