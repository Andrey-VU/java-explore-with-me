package ru.practicum.user.mapper;

import org.mapstruct.Mapper;
import ru.practicum.user.dto.NewUserRequest;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.dto.UserShortDto;
import ru.practicum.user.model.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User makeUser(NewUserRequest newUserRequest);

    UserDto makeDto(User user);

    UserShortDto makeShort(User user);

    UserShortDto makeShortFromDto(UserDto userDto);

    User makeUserFromDto(UserDto userDto);
}
