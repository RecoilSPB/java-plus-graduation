package ru.yandex.practicum.event.controller;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.event.EventFullDto;
import ru.yandex.practicum.dto.event.UpdateEventAdminDto;
import ru.yandex.practicum.event.facade.EventFacade;

import java.time.LocalDateTime;
import java.util.List;

import static ru.yandex.practicum.utils.JsonFormatPattern.JSON_FORMAT_PATTERN_FOR_TIME;

@RestController
@RequestMapping("/admin/events")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AdminEventController {

    final EventFacade eventFacade;

    @GetMapping
    public List<EventFullDto> getEvents(@RequestParam(required = false) List<Long> users,
                                        @RequestParam(required = false) List<String> states,
                                        @RequestParam(required = false) List<Long> categories,
                                        @DateTimeFormat(pattern = JSON_FORMAT_PATTERN_FOR_TIME) @RequestParam(required = false) LocalDateTime rangeStart,
                                        @DateTimeFormat(pattern = JSON_FORMAT_PATTERN_FOR_TIME) @RequestParam(required = false) LocalDateTime rangeEnd,
                                        @RequestParam(required = false, defaultValue = "0") Integer from,
                                        @RequestParam(required = false, defaultValue = "10") Integer size) {
        return eventFacade.getEvents(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEvent(@PathVariable Long eventId,
                                    @Valid @RequestBody UpdateEventAdminDto event) {
        return eventFacade.updateEvent(eventId, event);
    }
}
