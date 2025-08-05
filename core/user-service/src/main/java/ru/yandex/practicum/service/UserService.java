package ru.yandex.practicum.service;

import org.springframework.data.domain.Pageable;
import ru.yandex.practicum.dto.user.UserDto;
import ru.yandex.practicum.dto.user.UserShortDto;
import ru.yandex.practicum.exception.ConflictException;
import ru.yandex.practicum.exception.NotFoundException;

import java.util.List;

public interface UserService {

    UserDto addUser(UserDto newUserDto) throws ConflictException;

    UserShortDto getUserById(Long userId) throws NotFoundException;

    List<UserShortDto> getUsersByIdList(List<Long> ids, Pageable page);

    void deleteUser(Long userId);
}
