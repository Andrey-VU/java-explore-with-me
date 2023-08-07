package ru.practicum.user.model;

import lombok.AllArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.user.dto.NewUserRequest;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.dto.UserShortDto;
import ru.practicum.user.mapper.UserMapperImpl;
import ru.practicum.user.repo.UserRepo;

@Slf4j
@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService{
    private final UserRepo userRepo;
    private UserMapperImpl userMapper;

    @Override
    public UserDto create(NewUserRequest newUserRequest) {
        User user = userRepo.save(userMapper.makeUser(newUserRequest));
        return userMapper.makeDto(user);
    }

    @Override
    public UserDto activate(UserDto userDto) {
        return null;
    }

    @Override
    public UserDto read(UserShortDto userShortDto) {
        return null;
    }

    @Override
    public void delete(UserShortDto userShortDto) {

    }
}
