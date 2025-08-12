package ru.yandex.practicum.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.client.RequestClient;
import ru.yandex.practicum.dto.request.EventRequestCountDto;
import ru.yandex.practicum.dto.request.EventRequestDto;
import ru.yandex.practicum.dto.request.EventRequestStatus;
import ru.yandex.practicum.facade.EventRequestFacade;

import java.util.List;

@RestController
@RequestMapping(path = "/internal/api/requests")
@RequiredArgsConstructor
public class ClientController implements RequestClient {

    private final EventRequestFacade eventRequestFacade;

    @Override
    public List<EventRequestDto> getByStatus(Long eventId, EventRequestStatus status) {
        return eventRequestFacade.findAllByEventIdAndStatus(eventId, status);
    }

    @Override
    public List<EventRequestDto> getByIds(List<Long> ids) {
        return eventRequestFacade.getByIds(ids);
    }

    @Override
    public List<EventRequestCountDto> getConfirmedCount(List<Long> ids) {
        return eventRequestFacade.getConfirmedCount(ids);
    }

    @Override
    public List<EventRequestDto> updateStatus(EventRequestStatus status, List<Long> ids) {
        return eventRequestFacade.updateStatus(status, ids);
    }
}