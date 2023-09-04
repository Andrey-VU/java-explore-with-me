package ru.practicum.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class UserShortDto {
    private Long id;
    private String name;
}
