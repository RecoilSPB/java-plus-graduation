package ru.yandex.practicum.event.service;

import ru.yandex.practicum.dto.event.NewEventDto;
import ru.yandex.practicum.dto.event.UpdateEventUserDto;
import ru.yandex.practicum.dto.location.LocationDto;
import ru.yandex.practicum.event.model.Event;

import java.util.List;

public interface PrivateEventService {

    Event addEvent(Long userId, NewEventDto event, Long locationId);

    Event updateEvent(Long userId, Long eventId, UpdateEventUserDto event, LocationDto location);

    List<Event> getUserEvents(Long userId, Integer from, Integer count);

    Event getEventById(Long userId, Long eventId);
}
