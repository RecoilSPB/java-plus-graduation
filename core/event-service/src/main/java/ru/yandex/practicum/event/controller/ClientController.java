package ru.yandex.practicum.event.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.client.EventClient;
import ru.yandex.practicum.dto.event.EventFullDto;
import ru.yandex.practicum.event.facade.EventFacade;

import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
@RequestMapping(path = "/internal/api/events")
@RequiredArgsConstructor
public class ClientController implements EventClient {

    EventFacade eventFacade;

    @Override
    public EventFullDto getById(Long eventId) {
        return eventFacade.getEventById(eventId);
    }

    @Override
    public List<EventFullDto> getByLocation(Long locationId) {
        return eventFacade.getByLocation(locationId);
    }
}