package ru.yandex.practicum.event.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.event.EventFullDto;
import ru.yandex.practicum.dto.event.EventShortDto;
import ru.yandex.practicum.event.facade.EventFacade;

import java.util.List;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PublicEventController {

    final EventFacade eventFacade;

    @GetMapping("/{id}")
    public EventFullDto getEventById(@PathVariable Long id,
                                     HttpServletRequest request) {
        return eventFacade.getEventById(id, request);
    }

    @GetMapping
    public List<EventShortDto> getFilteredEvents(@RequestParam(required = false) String text,
                                                 @RequestParam(required = false) List<Long> categories,
                                                 @RequestParam(required = false) Boolean paid,
                                                 @RequestParam(required = false) String rangeStart,
                                                 @RequestParam(required = false) String rangeEnd,
                                                 @RequestParam(required = false, defaultValue = "false") Boolean available,
                                                 @RequestParam(required = false, defaultValue = "EVENT_DATE") String sort,
                                                 @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                 @Positive @RequestParam(defaultValue = "10") Integer count,
                                                 HttpServletRequest request) {
        return eventFacade.getFilteredEvents(text, categories, paid, rangeStart, rangeEnd, available, sort, from, count,
                request.getRequestURI(), request.getRemoteAddr());
    }
}