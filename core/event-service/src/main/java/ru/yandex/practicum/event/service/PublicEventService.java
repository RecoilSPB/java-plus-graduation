package ru.yandex.practicum.event.service;

import ru.yandex.practicum.event.model.Event;

import java.util.List;

public interface PublicEventService {
    Event getEventById(Long eventId);

    List<Event> getFilteredEvents(String text,
                                  List<Long> categories,
                                  Boolean paid,
                                  String rangeStart,
                                  String rangeEnd,
                                  Boolean onlyAvailable,
                                  String sort,
                                  Integer from,
                                  Integer size,
                                  String uri,
                                  String ip);
}
