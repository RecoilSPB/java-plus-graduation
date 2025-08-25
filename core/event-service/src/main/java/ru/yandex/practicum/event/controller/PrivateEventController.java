package ru.yandex.practicum.event.controller;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.event.*;
import ru.yandex.practicum.dto.request.EventRequestDto;
import ru.yandex.practicum.event.facade.EventFacade;

import java.util.List;

@Validated
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
@RequestMapping("/users/{userId}/events")
@RequiredArgsConstructor
public class PrivateEventController {

    EventFacade eventFacade;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto addEvent(@PathVariable Long userId,
                                 @Valid @RequestBody NewEventDto event) {
        return eventFacade.addEvent(userId, event);
    }

    @GetMapping
    public List<EventShortDto> getUserEvents(@PathVariable Long userId,
                                             @RequestParam(defaultValue = "0") Integer from,
                                             @RequestParam(defaultValue = "10") Integer count) {
        return eventFacade.getEventsByUserId(userId, from, count);
    }


    @GetMapping("/{eventId}")
    public EventFullDto getEventById(@PathVariable Long userId,
                                     @PathVariable Long eventId) {
        return eventFacade.getEventById(userId, eventId);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEvent(@PathVariable Long userId,
                                    @PathVariable Long eventId,
                                    @Valid @RequestBody UpdateEventUserDto event) {
        return eventFacade.updateEvent(userId, eventId, event);
    }

    @GetMapping(path = "/{eventId}/requests")
    public List<EventRequestDto> getParticipationRequests(@PathVariable("userId") Long userId,
                                                          @PathVariable("eventId") Long eventId) {
        return eventFacade.getEventAllParticipationRequests(eventId, userId);
    }

    //Изменение статуса (подтверждена, отменена) заявок на участие в событии текущего пользователя
    @PatchMapping(path = "/{eventId}/requests")
    public EventRequestStatusUpdateResultDto updatedEventRequestStatus(@PathVariable("userId") Long userId,
                                                                       @PathVariable("eventId") Long eventId,
                                                                       @RequestBody EventRequestStatusUpdateRequestDto request) {
        return eventFacade.changeEventState(userId, eventId, request);
    }
}
