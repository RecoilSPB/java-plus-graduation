package ru.yandex.practicum.event.controller;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.event.EventAdminFilterParamsDto;
import ru.yandex.practicum.dto.event.EventFullDto;
import ru.yandex.practicum.dto.event.UpdateEventAdminDto;
import ru.yandex.practicum.event.facade.EventFacade;

import java.util.List;

@RestController
@RequestMapping("/admin/events")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AdminEventController {

    final EventFacade eventFacade;

    @GetMapping
    public List<EventFullDto> getEvents(@Valid EventAdminFilterParamsDto filters,
                                        @RequestParam(required = false, defaultValue = "0") Integer from,
                                        @RequestParam(required = false, defaultValue = "10") Integer size) {
        return eventFacade.getEvents(filters, from, size);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEvent(@PathVariable Long eventId,
                                    @Valid @RequestBody UpdateEventAdminDto event) {
        return eventFacade.updateEvent(eventId, event);
    }
}
