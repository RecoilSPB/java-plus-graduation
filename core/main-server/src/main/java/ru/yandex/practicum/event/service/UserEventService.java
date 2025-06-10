package ru.yandex.practicum.event.service;

import ru.yandex.practicum.event.dto.EventFullDto;
import ru.yandex.practicum.event.dto.EventShortDto;
import ru.yandex.practicum.event.dto.NewEventDto;
import ru.yandex.practicum.event.dto.UpdateEventUserRequest;
import ru.yandex.practicum.exception.ConflictException;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.exception.ValidationException;
import ru.yandex.practicum.exception.WrongDataException;

import java.util.List;

public interface UserEventService {

    EventFullDto addEvent(Long userId, NewEventDto event) throws ValidationException, WrongDataException, NotFoundException;

    EventFullDto updateEvent(Long userId, Long eventId, UpdateEventUserRequest event) throws ConflictException, NotFoundException, ValidationException, WrongDataException;

    List<EventShortDto> getUserEvents(Long userId, Integer from, Integer count) throws NotFoundException;

    EventFullDto getEventById(Long userId, Long eventId) throws NotFoundException, ValidationException;
}
