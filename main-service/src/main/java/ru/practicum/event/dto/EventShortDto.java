package ru.practicum.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.user.dto.UserShortDto;

import java.time.LocalDateTime;

import static ru.practicum.utils.MainConstants.DATE_TIME_FORMAT;

@Data
public class EventShortDto {
    private Long id;
    private String annotation;
    private CategoryDto category;
    private Long confirmedRequests;
    @JsonFormat(pattern = DATE_TIME_FORMAT)
    private LocalDateTime eventDate;
    private UserShortDto initiator;
    private Boolean paid;
    private String title;
    private Long views;

//    {   "annotation": "Эксклюзивность нашего шоу гарантирует привлечение максимальной зрительской аудитории",
//        "category": { "id": 1, "name": "Концерты"  },
//        "confirmedRequests": 5,
//        "eventDate": "2024-03-10 14:30:00",
//        "id": 1,
//        "initiator": { "id": 3, "name": "Фёдоров Матвей"  },
//        "paid": true,
//        "title": "Знаменитое шоу 'Летающая кукуруза'",
//        "views": 999     },
}