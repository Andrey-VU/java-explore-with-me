package ru.practicum.event.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.event.enums.EventState;
import ru.practicum.event.location.Location;
import ru.practicum.user.dto.UserShortDto;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@ToString
@Getter
@Setter
@EqualsAndHashCode
@Table(name = "events", schema = "public")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "annotation")
    private String annotation;

    @Column(name = "category")
    private CategoryDto category;

    @Column(name = "created")
    private LocalDateTime createdOn;

    @Column(name = "published")
    private LocalDateTime publishedOn;

    @Column(name = "description")
    private String description;

    @Column(name = "event_date")
    private LocalDateTime eventDate;

    @Column(name = "initiator")
    private UserShortDto initiator;

    @Column(name = "location")
    private Location location;

    @Column(name = "paid")
    private Boolean paid;

    @Column(name = "participant_limit")
    private Integer participantLimit;

    @Column(name = "request_moderation")
    private Boolean requestModeration;

    @Column(name = "event_state")
    private EventState eventState;

    @Column(name = "title")
    private String title;
}
