package ru.yandex.practicum.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.request.EventRequestDto;
import ru.yandex.practicum.facade.EventRequestFacade;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}")
public class RequestController {

    final EventRequestFacade requestFacade;

    @PostMapping("/requests")
    @ResponseStatus(HttpStatus.CREATED)
    public EventRequestDto addEventRequest(@PathVariable Long userId,
                                           @RequestParam Long eventId) {
        return requestFacade.addRequest(userId, eventId);
    }

    @GetMapping("/requests")
    public List<EventRequestDto> getUserRequests(@PathVariable Long userId) {
        return requestFacade.getUserRequests(userId);
    }

    @PatchMapping("/requests/{requestId}/cancel")
    public EventRequestDto cancelRequest(@PathVariable Long userId,
                                         @PathVariable Long requestId) {
        return requestFacade.cancelRequest(userId, requestId);
    }
}
