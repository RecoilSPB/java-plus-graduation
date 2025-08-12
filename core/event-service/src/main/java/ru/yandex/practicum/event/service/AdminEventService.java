package ru.yandex.practicum.event.service;

import ru.yandex.practicum.dto.event.UpdateEventAdminDto;
import ru.yandex.practicum.dto.location.LocationDto;
import ru.yandex.practicum.event.model.Event;

import java.time.LocalDateTime;
import java.util.List;

public interface AdminEventService {

    List<Event> getEvents(List<Long> users,
                          List<String> states,
                          List<Long> categories,
                          LocalDateTime rangeStart,
                          LocalDateTime rangeEnd,
                          Integer from,
                          Integer size);

    Event updateEvent(Long eventId, UpdateEventAdminDto event, LocationDto location);

}
