package ru.practicum.event.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class LocationDto {
    @NotNull
    private float lat;
    @NotNull
    private float lon;
}
