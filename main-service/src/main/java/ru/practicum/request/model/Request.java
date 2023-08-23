package ru.practicum.request.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import ru.practicum.event.model.Event;
import ru.practicum.request.enums.RequestState;
import ru.practicum.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

import static ru.practicum.utils.MainConstants.DATE_TIME_FORMAT;

@Entity
@ToString
@Getter
@Setter
@EqualsAndHashCode
@Table(name = "requests", schema = "public")
@Builder
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonFormat(pattern = DATE_TIME_FORMAT)
    @Column(name = "created")
    private LocalDateTime created;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    @ToString.Exclude
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requester_id")
    @ToString.Exclude
    private User requester;

    @Column(name = "status")
    private RequestState status;
}
