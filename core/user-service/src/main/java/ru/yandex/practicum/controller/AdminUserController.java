package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.user.UserShortDto;
import ru.yandex.practicum.exception.ConflictException;
import ru.yandex.practicum.service.UserService;
import ru.yandex.practicum.dto.user.UserDto;

import java.util.List;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequestMapping("/admin/users")
public class AdminUserController {

    final UserService userService;

    @GetMapping
    public List<UserShortDto> getUsersList(@RequestParam(required = false) List<Long> ids,
                                               @RequestParam(required = false, defaultValue = "0") Integer from,
                                               @RequestParam(required = false, defaultValue = "10") Integer size) {
        return userService.getUsersByIdList(ids, PageRequest.of(from, size));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto addUser(@Valid @RequestBody UserDto newUser) throws ConflictException {
        return userService.addUser(newUser);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
    }

}
