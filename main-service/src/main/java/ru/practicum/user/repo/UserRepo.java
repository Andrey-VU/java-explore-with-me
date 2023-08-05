package ru.practicum.user.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.user.model.User;

public interface UserRepo extends JpaRepository<User, Long> {
}
