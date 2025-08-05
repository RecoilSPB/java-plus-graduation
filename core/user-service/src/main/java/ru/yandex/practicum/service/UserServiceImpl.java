package ru.yandex.practicum.service;


import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.dto.user.UserShortDto;
import ru.yandex.practicum.exception.ConflictException;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.dto.user.UserDto;
import ru.yandex.practicum.mapper.UserMapper;
import ru.yandex.practicum.model.User;
import ru.yandex.practicum.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserServiceImpl implements UserService {

    final UserRepository userRepository;
    final UserMapper userMapper;

    @Override
    @Transactional
    public UserDto addUser(UserDto newUserDto) throws ConflictException {
        if (userRepository.existsByName(newUserDto.getName())) {
            throw new ConflictException(String.format("Пользователь %s уже существует", newUserDto.getName()));
        }
        User savedUser = userRepository.save(userMapper.toEntity(newUserDto));
        return userMapper.toDto(savedUser);
    }

    @Override
    public UserShortDto getUserById(Long userId) throws NotFoundException {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException(
                String.format("Пользователь с id = %d не найден", userId))
        );
        log.info("getById result user = {}", user);
        return userMapper.toShortDto(user);
    }

    @Override
    public List<UserShortDto> getUsersByIdList(List<Long> ids, Pageable page) {
        List<User> users = (ids == null || ids.isEmpty()) ?
                userRepository.findAll(page).getContent() :
                userRepository.findAllByIdsPageable(ids, page);
        return users.stream()
                .map(userMapper::toShortDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }
}
