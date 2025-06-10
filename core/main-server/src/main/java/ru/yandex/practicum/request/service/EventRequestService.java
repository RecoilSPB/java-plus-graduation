package ru.yandex.practicum.request.service;

import ru.yandex.practicum.exception.ConflictException;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.exception.ValidationException;
import ru.yandex.practicum.request.dto.EventRequestDto;

import java.util.List;

public interface EventRequestService {

    EventRequestDto addRequest(Long userId, Long eventId) throws ConflictException, NotFoundException;

    List<EventRequestDto> getUserRequests(Long userId) throws NotFoundException;

    List<EventRequestDto> getRequestsByEventId(Long userId, Long eventId) throws ValidationException, NotFoundException;

    EventRequestDto updateRequest(Long userId,
                                  Long eventId,
                                  EventRequestDto request) throws ConflictException, ValidationException, NotFoundException;

    EventRequestDto cancelRequest(Long userId, Long requestId) throws NotFoundException, ValidationException;
}
