package ru.yandex.practicum.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.client.UserClient;
import ru.yandex.practicum.dto.user.UserDto;
import ru.yandex.practicum.dto.user.UserShortDto;
import ru.yandex.practicum.service.UserService;

import java.util.List;

@RestController
@RequestMapping(path = "/internal/api/users")
@RequiredArgsConstructor
public class ClientUserController implements UserClient {
    private final UserService userService;


    @Override
    public UserShortDto getById(Long userId) {
        return userService.getUserById(userId);
    }

    @Override
    public List<UserDto> getByIds(List<Long> ids,
                                  @RequestParam(required = false, defaultValue = "0") Integer from,
                                  @RequestParam(required = false, defaultValue = "10") Integer size) {
        return userService.getUsersByIdList(ids, PageRequest.of(from, size));
    }
}
