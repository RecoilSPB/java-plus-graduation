package ru.yandex.practicum.facade;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.client.EventClient;
import ru.yandex.practicum.client.UserClient;
import ru.yandex.practicum.dto.event.EventFullDto;
import ru.yandex.practicum.dto.request.EventRequestCountDto;
import ru.yandex.practicum.dto.request.EventRequestDto;
import ru.yandex.practicum.dto.request.EventRequestStatus;
import ru.yandex.practicum.dto.user.UserShortDto;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.service.EventRequestService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventRequestFacadeImpl implements EventRequestFacade {

    private final UserClient userClient;
    private final EventClient eventClient;

    private final EventRequestService eventRequestService;

    // RequestController
    @Override
    public EventRequestDto addRequest(Long userId, Long eventId) {
        checkAndGetUserById(userId);
        EventFullDto eventFullDto = checkAndGetEventById(eventId);
        return eventRequestService.addRequest(userId, eventFullDto);
    }

    @Override
    public List<EventRequestDto> getUserRequests(Long userId) {
        checkAndGetUserById(userId);
        return eventRequestService.getUserRequests(userId);
    }

    @Override
    public EventRequestDto cancelRequest(Long userId, Long requestId) {
        checkAndGetUserById(userId);
        return eventRequestService.cancelRequest(userId, requestId);
    }

    // ClientController
    @Override
    public List<EventRequestDto> findAllByEventIdAndStatus(Long eventId, EventRequestStatus status) {
        return eventRequestService.findAllByEventIdAndStatus(eventId, status);
    }

    @Override
    public List<EventRequestDto> getByIds(List<Long> ids) {
        return eventRequestService.getByIds(ids);
    }

    @Override
    public List<EventRequestCountDto> getConfirmedCount(List<Long> ids) {
        return eventRequestService.getConfirmedCount(ids);
    }

    @Override
    public List<EventRequestDto> updateStatus(EventRequestStatus status, List<Long> ids) {
        return eventRequestService.updateStatus(status, ids);
    }

    private void checkAndGetUserById(Long userId) {
        UserShortDto user = userClient.getById(userId);
        if (user == null) {
            throw new NotFoundException("Такого пользователя не существует: " + userId);
        }
    }

    private EventFullDto checkAndGetEventById(Long eventId) {
        EventFullDto event = eventClient.getById(eventId);
        if (event == null) {
            throw new NotFoundException("Такого события не существует: " + eventId);
        }

        return event;
    }
}
