package ru.practicum.comments.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import ru.practicum.event.model.Event;
import ru.practicum.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

import static ru.practicum.utils.MainConstants.DATE_TIME_FORMAT;

@Entity
@ToString
@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "comments", schema = "public")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "event_id")
    @ToString.Exclude
    private Event event;

    @ManyToOne
    @JoinColumn(name = "author_id")
    @ToString.Exclude
    private User author;

    private String text;

    @Enumerated(EnumType.STRING)
    private Reaction reaction;

    @Enumerated(EnumType.STRING)
    private CommentState state;

    private String proposal;

    @JsonFormat(pattern = DATE_TIME_FORMAT)
    private LocalDateTime created;

    private Boolean isToxic = false;
}
