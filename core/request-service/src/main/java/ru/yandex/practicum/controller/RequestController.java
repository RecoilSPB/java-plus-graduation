package ru.yandex.practicum.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.request.EventRequestDto;
import ru.yandex.practicum.service.EventRequestService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}")
public class RequestController {

    final EventRequestService requestService;

    @PostMapping("/requests")
    @ResponseStatus(HttpStatus.CREATED)
    public EventRequestDto addEventRequest(@PathVariable Long userId,
                                           @RequestParam Long eventId) {
        return requestService.addRequest(userId, eventId);
    }

    @GetMapping("/requests")
    public List<EventRequestDto> getUserRequests(@PathVariable Long userId) {
        return requestService.getUserRequests(userId);
    }

    @GetMapping("/events/{eventId}/requests")
    public List<EventRequestDto> getRequestsByEventId(@PathVariable Long userId,
                                                      @PathVariable Long eventId) {
        return requestService.getRequestsByEventId(userId, eventId);
    }

    @PatchMapping("/events/{eventId}/requests")
    public EventRequestDto updateRequest(@PathVariable Long userId,
                                         @PathVariable Long eventId,
                                         @RequestBody EventRequestDto request) {
        return requestService.updateRequest(userId, eventId, request);
    }

    @PatchMapping("/requests/{requestId}/cancel")
    public EventRequestDto cancelRequest(@PathVariable Long userId,
                                         @PathVariable Long requestId) {
        return requestService.cancelRequest(userId, requestId);
    }
}
