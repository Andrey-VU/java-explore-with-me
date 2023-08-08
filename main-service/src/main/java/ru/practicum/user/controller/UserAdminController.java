package ru.practicum.user.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.user.dto.NewUserRequest;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.model.UserService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Validated
@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping(path = "/admin/users")
public class UserAdminController {
    private final UserService userService;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto createNewUser(@Valid @RequestBody NewUserRequest newUserRequest) {
        log.info("Add new user {} - STARTED", newUserRequest);
        UserDto newUser = userService.create(newUserRequest);
        log.info("A new user has been registered {}", newUser);
        return newUser;
    }

    @GetMapping
    public List<UserDto> getUsers(@RequestParam (required = false) List<Long> ids,
                                  @Positive @RequestParam (required = false, defaultValue = "10") int size,
                                  @PositiveOrZero @RequestParam (required = false, defaultValue = "0") int from){
        log.info("STARTED Getting info about {} users with parameters: \n size {} \n from {}  ", ids.size(),
            size, from);
        List<UserDto> users = userService.getUsers(ids, size, from);
        return users;
    };

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable("userId") Long userId) {
        log.info("deleteUser: {} userId - STARTED", userId);
        userService.delete(userId);
        }


}
