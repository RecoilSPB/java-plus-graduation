package ru.yandex.practicum.facade;

import ru.yandex.practicum.dto.request.EventRequestCountDto;
import ru.yandex.practicum.dto.request.EventRequestDto;
import ru.yandex.practicum.dto.request.EventRequestStatus;

import java.util.List;

public interface EventRequestFacade {

    // RequestController
    EventRequestDto addRequest(Long userId, Long eventId);

    List<EventRequestDto> getUserRequests(Long userId);

    EventRequestDto cancelRequest(Long userId, Long requestId);


    // ClientController
    List<EventRequestDto> findAllByEventIdAndStatus(Long eventId, EventRequestStatus status);

    List<EventRequestDto> getByIds(List<Long> ids);

    List<EventRequestCountDto> getConfirmedCount(List<Long> ids);

    List<EventRequestDto> updateStatus(EventRequestStatus status, List<Long> ids);
}
