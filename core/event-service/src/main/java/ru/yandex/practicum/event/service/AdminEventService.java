package ru.yandex.practicum.event.service;

import ru.yandex.practicum.event.dto.EventFullDto;
import ru.yandex.practicum.event.dto.UpdateEventAdminRequest;
import ru.yandex.practicum.exception.ConflictException;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.exception.ValidationException;
import ru.yandex.practicum.exception.WrongDataException;

import java.time.LocalDateTime;
import java.util.List;

public interface AdminEventService {

    List<EventFullDto> getEvents(List<Long> users, List<String> states, List<Long> categories, LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from, Integer size) throws ValidationException;

    EventFullDto updateEvent(Long eventId, UpdateEventAdminRequest event) throws ConflictException, ValidationException, NotFoundException, WrongDataException;

}
