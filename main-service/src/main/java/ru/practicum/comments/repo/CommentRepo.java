package ru.practicum.comments.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.comments.model.Comment;

public interface CommentRepo extends JpaRepository<Comment, Long> {
}
