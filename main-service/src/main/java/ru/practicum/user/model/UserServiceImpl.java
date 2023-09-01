package ru.practicum.user.model;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exception.EwmConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.user.dto.NewUserRequest;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.mapper.UserMapper;
import ru.practicum.user.repo.UserRepo;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService{
    private final UserRepo userRepo;
    private UserMapper userMapper;

    @Override
    @Transactional
    public UserDto create(NewUserRequest newUserRequest) {
        isUserNameFree(newUserRequest.getName());
        User user = userRepo.save(userMapper.makeUser(newUserRequest));
        log.info("User {} was saved in repo", user);
        return userMapper.makeDto(user);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        log.info("User id {} delete: STARTED", id);
        userRepo.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getUsers(List<Long> ids, int size, int from) {
        List<UserDto> usersDto = new ArrayList<>();
        PageRequest pageRequest = PageRequest.of(from > 0 ? from / size : 0, size);

        if (ids == null || ids.size() == 0) {
            usersDto = userRepo.findAll(pageRequest).stream()
                .map(user -> userMapper.makeDto(user))
                .collect(Collectors.toList());
        } else {
            usersDto = userRepo.findAllByIdIn(ids, pageRequest).stream()
                .map(user -> userMapper.makeDto(user))
                .collect(Collectors.toList());
        }
        log.info("{} users was found", usersDto.size());
        return usersDto;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto get(Long userId) {
        return userMapper.makeDto(userRepo.findById(userId)
            .orElseThrow( () -> new NotFoundException("User id " + userId + " is NOT FOUND!")));
    }

    @Override
    @Transactional(readOnly = true)
    public User getUserById(Long requesterId) {
        return userRepo.findById(requesterId)
            .orElseThrow(() -> new NotFoundException("Пользователь " + requesterId + " НЕ НАЙДЕН!"));
    }

    @Transactional(readOnly = true)
    private void isUserNameFree(String name) {
        List<String> namesOfUsers = new ArrayList<>();
        for (User user : userRepo.findAll()) {
            namesOfUsers.add(user.getName());
        }
        if (namesOfUsers.contains(name)) {
            log.warn("попытка использовать занятое имя");
            throw new EwmConflictException("Имя занято. Использовать повторно невозможно");
        }
    }
}
