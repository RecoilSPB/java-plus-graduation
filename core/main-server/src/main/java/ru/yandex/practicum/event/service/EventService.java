package ru.yandex.practicum.event.service;

import ru.yandex.practicum.event.dto.EventFullDto;
import ru.yandex.practicum.event.dto.EventShortDto;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.exception.ValidationException;

import java.util.List;

public interface EventService {
    EventFullDto getEventById(Long eventId, String uri, String ip) throws NotFoundException;

    List<EventShortDto> getFilteredEvents(String text,
                                          List<Long> categories,
                                          Boolean paid,
                                          String rangeStart,
                                          String rangeEnd,
                                          Boolean onlyAvailable,
                                          String sort,
                                          Integer from,
                                          Integer size,
                                          String uri,
                                          String ip) throws ValidationException;
}
