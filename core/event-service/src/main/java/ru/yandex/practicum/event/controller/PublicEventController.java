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
import ru.yandex.practicum.dto.event.RecommendedEventDto;
import ru.yandex.practicum.event.facade.EventFacade;

import java.util.List;
import java.util.stream.Stream;

@Validated
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class PublicEventController {

    EventFacade eventFacade;

    @GetMapping("/{id}")
    public EventFullDto getEventById(@PathVariable Long id,
                                     @RequestHeader("X-EWM-USER-ID") Long userId,
                                     HttpServletRequest request) {
        return eventFacade.getEventById(id, userId, request);
    }

    @GetMapping
    public List<EventShortDto> getFilteredEvents(@Valid EventPublicFilterParamsDto filters,
                                                 @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                 @Positive @RequestParam(defaultValue = "10") Integer count,
                                                 HttpServletRequest request) {
        return eventFacade.getFilteredEvents(filters, from, count, request);
    }

    @GetMapping("/recommendations")
    public Stream<RecommendedEventDto> getRecommendations(@RequestHeader("X-EWM-USER-ID") Long userId,
                                                          @PositiveOrZero @RequestParam(defaultValue = "10") int limit) {
        return eventFacade.getRecommendations(userId, limit);
    }
}