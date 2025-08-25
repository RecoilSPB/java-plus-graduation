package ru.yandex.practicum.service;

import ru.yandex.practicum.dto.event.EventFullDto;
import ru.yandex.practicum.dto.request.EventRequestCountDto;
import ru.yandex.practicum.dto.request.EventRequestDto;
import ru.yandex.practicum.dto.request.EventRequestStatus;

import java.util.List;

public interface EventRequestService {
    // RequestController
    EventRequestDto addRequest(Long userId, EventFullDto event);

    List<EventRequestDto> getUserRequests(Long userId);

    EventRequestDto cancelRequest(Long userId, Long requestId);

    List<EventRequestDto> findAllByEventIdAndStatus(Long eventId, EventRequestStatus status);

    List<EventRequestDto> getByIds(List<Long> ids);

    List<EventRequestCountDto> getConfirmedCount(List<Long> ids);

    List<EventRequestDto> updateStatus(EventRequestStatus status, List<Long> ids);
}
