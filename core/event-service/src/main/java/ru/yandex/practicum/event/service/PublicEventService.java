package ru.yandex.practicum.event.service;

import jakarta.servlet.http.HttpServletRequest;
import ru.yandex.practicum.dto.event.EventPublicFilterParamsDto;
import ru.yandex.practicum.dto.location.LocationDto;
import ru.yandex.practicum.event.model.Event;

import java.util.List;

public interface PublicEventService {

    Event getEventById(Long eventId);

    List<Event> getFilteredEvents(EventPublicFilterParamsDto filters,
                                  int from,
                                  int size,
                                  List<LocationDto> locations,
                                  HttpServletRequest request);
}
