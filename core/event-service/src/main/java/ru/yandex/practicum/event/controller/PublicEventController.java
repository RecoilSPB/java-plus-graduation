package ru.yandex.practicum.event.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.event.EventFullDto;
import ru.yandex.practicum.dto.event.EventPublicFilterParamsDto;
import ru.yandex.practicum.dto.event.EventShortDto;
import ru.yandex.practicum.event.facade.EventFacade;

import java.util.List;

@Validated
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class PublicEventController {

    EventFacade eventFacade;

    @GetMapping("/{id}")
    public EventFullDto getEventById(@PathVariable Long id,
                                     HttpServletRequest request) {
        return eventFacade.getEventById(id, request);
    }

    @GetMapping
    public List<EventShortDto> getFilteredEvents(@Valid EventPublicFilterParamsDto filters,
                                                 @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                 @Positive @RequestParam(defaultValue = "10") Integer count,
                                                 HttpServletRequest request) {
        return eventFacade.getFilteredEvents(filters, from, count, request);
    }
}