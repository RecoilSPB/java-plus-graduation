package ru.yandex.practicum.event.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.client.EventClient;
import ru.yandex.practicum.dto.event.EventFullDto;
import ru.yandex.practicum.event.facade.EventFacade;

import java.util.List;

@RestController
@RequestMapping(path = "/internal/api/events")
@RequiredArgsConstructor
public class ClientController implements EventClient {
    private final EventFacade eventFacade;

    @Override
    public EventFullDto getById(Long eventId) {
        return eventFacade.getEventById(eventId);
    }

    @Override
    public List<EventFullDto> getByLocation(Long locationId) {
        return eventFacade.getByLocation(locationId);
    }
}