package ru.yandex.practicum.event.controller;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.event.dto.EventFullDto;
import ru.yandex.practicum.event.dto.UpdateEventAdminRequest;
import ru.yandex.practicum.event.service.AdminEventService;
import ru.yandex.practicum.exception.ConflictException;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.exception.ValidationException;
import ru.yandex.practicum.exception.WrongDataException;

import java.time.LocalDateTime;
import java.util.List;

import static ru.yandex.practicum.utils.JsonFormatPattern.JSON_FORMAT_PATTERN_FOR_TIME;

@RestController
@RequestMapping("/admin/events")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AdminEventController {

    final AdminEventService eventService;

    @GetMapping
    public List<EventFullDto> getEvents(@RequestParam(required = false) List<Long> users,
                                        @RequestParam(required = false) List<String> states,
                                        @RequestParam(required = false) List<Long> categories,
                                        @DateTimeFormat(pattern = JSON_FORMAT_PATTERN_FOR_TIME) @RequestParam(required = false) LocalDateTime rangeStart,
                                        @DateTimeFormat(pattern = JSON_FORMAT_PATTERN_FOR_TIME) @RequestParam(required = false) LocalDateTime rangeEnd,
                                        @RequestParam(required = false, defaultValue = "0") Integer from,
                                        @RequestParam(required = false, defaultValue = "10") Integer size) throws ValidationException {
        return eventService.getEvents(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEvent(@PathVariable Long eventId,
                                    @Valid @RequestBody UpdateEventAdminRequest event) throws ValidationException, ConflictException, WrongDataException, NotFoundException {
        return eventService.updateEvent(eventId, event);
    }
}
