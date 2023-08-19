package ru.practicum.user.model;

import ru.practicum.user.dto.NewUserRequest;
import ru.practicum.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto create(NewUserRequest newUserRequest);
    void delete(Long id);
    List<UserDto> getUsers(List<Long> ids, int size, int from);

    UserDto get(Long userId);
}
