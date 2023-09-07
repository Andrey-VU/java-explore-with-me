package ru.practicum.comments.repo;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.comments.model.Comment;
import ru.practicum.comments.model.Reaction;

import java.time.LocalDateTime;
import java.util.List;

public interface CommentRepo extends JpaRepository<Comment, Long> {
    int countByIdAndAuthorId(Long commentId, Long userId);

    List<Comment> findByEventIdAndCreatedAfterAndCreatedBefore(Long eventId, LocalDateTime start, LocalDateTime end,
                                                               PageRequest pageRequest);

    List<Comment> findByEventIdAndCreatedAfterAndCreatedBeforeAndTextContainingIgnoreCase(Long eventId,
                                                                                          LocalDateTime start,
                                                                                          LocalDateTime end,
                                                                                          String text,
                                                                                          PageRequest pageRequest);

    List<Comment> findByEventIdAndCreatedAfterAndCreatedBeforeAndReaction(Long eventId, LocalDateTime start,
                                                                          LocalDateTime end, Reaction reaction,
                                                                          PageRequest pageRequest);

    List<Comment> findByEventIdAndCreatedAfterAndCreatedBeforeAndReactionAndTextContainingIgnoreCase
        (Long eventId, LocalDateTime start, LocalDateTime end, Reaction reaction, String text, PageRequest pageRequest);
}
