package ru.practicum.user.model;

import ru.practicum.user.dto.NewUserRequest;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.dto.UserShortDto;

public interface UserService {
//должно быть настроено управление пользователями — добавление, активация, просмотр и удаление.
    UserDto create(NewUserRequest newUserRequest);
    UserDto activate(UserDto userDto);
    UserDto read(UserShortDto userShortDto);
    void delete(UserShortDto userShortDto);
}
