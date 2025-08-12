package ru.yandex.practicum.controller;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.comment.CommentDto;
import ru.yandex.practicum.service.CommentService;

import java.util.Collection;

@RequiredArgsConstructor
@RestController
@RequestMapping("/events/{eventId}/comments")
public class CommentController {

    private final CommentService service;

    @GetMapping
    public Collection<CommentDto> getByEvent(
            @PathVariable Long eventId,
            @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(defaultValue = "10") @Positive Integer size) {
        return service.getAllEventComments(eventId, from, size);
    }
}

