package ru.practicum;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import javax.validation.constraints.NotBlank;

@EqualsAndHashCode
@Getter
public class EndpointHit {
    @NotBlank
    private String app;
    @NotBlank
    private String uri;
    @NotBlank
    private String ip;
    @NotBlank
    private String timestamp;
}
