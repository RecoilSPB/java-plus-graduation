package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.comment.CommentDto;
import ru.yandex.practicum.facade.CommentFacade;

import java.util.Collection;

@RequiredArgsConstructor
@RestController
@RequestMapping("/users/{userId}/comments")
@Slf4j
@Validated
public class PrivateCommentController {

    private final CommentFacade commentFacade;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto addComments(
            @PathVariable Long userId,
            @RequestParam @Positive Long eventId,
            @RequestBody @Validated CommentDto commentDto) {
        return commentFacade.addComment(commentDto, userId, eventId);
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(
            @PathVariable @NonNull Long commentId,
            @PathVariable @NonNull Long userId) {
        commentFacade.delete(userId, commentId);
    }

    @PatchMapping("/{commentId}")
    public CommentDto updateComment(
            @PathVariable Long userId,
            @PathVariable Long commentId,
            @RequestBody @Valid CommentDto commentDto) {
        return commentFacade.updateUserComment(userId, commentId, commentDto);
    }

    @GetMapping
    public Collection<CommentDto> getByUserComment(@PathVariable Long userId) {
        return commentFacade.getAllUserComments(userId);
    }
}