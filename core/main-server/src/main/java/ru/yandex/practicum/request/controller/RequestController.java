package ru.yandex.practicum.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.exception.ConflictException;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.exception.ValidationException;
import ru.yandex.practicum.request.dto.EventRequestDto;
import ru.yandex.practicum.request.service.EventRequestService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}")
public class RequestController {

    final EventRequestService requestService;

    @PostMapping("/requests")
    @ResponseStatus(HttpStatus.CREATED)
    public EventRequestDto addEventRequest(@PathVariable Long userId,
                                           @RequestParam Long eventId) throws ConflictException, NotFoundException {
        return requestService.addRequest(userId, eventId);
    }

    @GetMapping("/requests")
    public List<EventRequestDto> getUserRequests(@PathVariable Long userId) throws NotFoundException {
        return requestService.getUserRequests(userId);
    }

    @GetMapping("/events/{eventId}/requests")
    public List<EventRequestDto> getRequestsByEventId(@PathVariable Long userId,
                                                      @PathVariable Long eventId) throws ValidationException, NotFoundException {
        return requestService.getRequestsByEventId(userId, eventId);
    }

    @PatchMapping("/events/{eventId}/requests")
    public EventRequestDto updateRequest(@PathVariable Long userId,
                                         @PathVariable Long eventId,
                                         @RequestBody EventRequestDto request) throws ValidationException, ConflictException, NotFoundException {
        return requestService.updateRequest(userId, eventId, request);
    }

    @PatchMapping("/requests/{requestId}/cancel")
    public EventRequestDto cancelRequest(@PathVariable Long userId,
                                         @PathVariable Long requestId) throws ValidationException, NotFoundException {
        return requestService.cancelRequest(userId, requestId);
    }
}
