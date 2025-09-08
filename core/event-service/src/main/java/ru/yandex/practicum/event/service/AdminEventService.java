package ru.yandex.practicum.event.service;

import ru.yandex.practicum.dto.event.EventAdminFilterParamsDto;
import ru.yandex.practicum.dto.event.UpdateEventAdminDto;
import ru.yandex.practicum.dto.location.LocationDto;
import ru.yandex.practicum.event.model.Event;

import java.util.List;

public interface AdminEventService {

    List<Event> getEvents(EventAdminFilterParamsDto filters, int from, int size);

    Event updateEvent(Long eventId, UpdateEventAdminDto event, LocationDto location);

    List<Event> getByLocation(Long locationId);
}
