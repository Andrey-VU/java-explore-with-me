package ru.practicum.user.repo;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.user.model.User;

import java.util.List;

public interface UserRepo extends JpaRepository<User, Long> {
    List<User> findAllByIdIn(List<Long> ids, PageRequest pageRequest);
}
