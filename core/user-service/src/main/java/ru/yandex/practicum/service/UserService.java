package ru.yandex.practicum.service;

import org.springframework.data.domain.Pageable;
import ru.yandex.practicum.dto.user.UserDto;
import ru.yandex.practicum.dto.user.UserShortDto;

import java.util.List;

public interface UserService {

    UserDto addUser(UserDto newUserDto);

    UserShortDto getUserById(Long userId);

    List<UserDto> getUsersByIdList(List<Long> ids, Pageable page);

    void deleteUser(Long userId);
}
