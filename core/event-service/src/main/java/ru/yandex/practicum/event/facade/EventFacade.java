package ru.yandex.practicum.event.facade;

import jakarta.servlet.http.HttpServletRequest;
import ru.yandex.practicum.dto.event.*;
import ru.yandex.practicum.dto.request.EventRequestDto;

import java.util.List;

public interface EventFacade {
    EventFullDto addEvent(Long id, NewEventDto newEventDto);

    List<EventShortDto> getEventsByUserId(Long id, int from, int size);

    EventFullDto getEventById(Long userId, Long eventId);

    EventFullDto getEventById(Long eventId);

    EventFullDto updateEvent(Long userId, Long eventId, UpdateEventUserDto updateEventUserDto);

    EventFullDto updateEvent(Long eventId, UpdateEventAdminDto updateEventAdminDto);

    EventFullDto getEventById(Long eventId, HttpServletRequest request);

    List<EventFullDto> get(EventAdminFilterParamsDto filters, int from, int size);

    List<EventShortDto> get(EventPublicFilterParamsDto filters, int from, int size, HttpServletRequest request);

    List<EventRequestDto> getEventAllParticipationRequests(Long eventId, Long userId);

    EventRequestStatusUpdateResultDto changeEventState(Long userId, Long eventId,
                                                       EventRequestStatusUpdateRequestDto requestStatusUpdateRequest);

    List<EventFullDto> getByLocation(Long locationId);
}