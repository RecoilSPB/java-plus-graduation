package ru.yandex.practicum.facade;

import ru.yandex.practicum.dto.comment.CommentDto;
import ru.yandex.practicum.dto.comment.GetCommentsAdminRequest;

import java.util.Collection;

public interface CommentFacade {
    // Admin Controller
    Collection<CommentDto> getAllEventComments(GetCommentsAdminRequest getCommentsAdminRequest);

    void delete(Long commentId);

    // Public Comment Controller
    Collection<CommentDto> getAllEventComments(Long eventId, Integer from, Integer size);

    // Private Comment Controller
    CommentDto addComment(CommentDto commentDto, Long userId, Long eventId);

    void delete(Long userId, Long commentId);

    CommentDto updateUserComment(Long userId, Long commentId, CommentDto commentDto);

    Collection<CommentDto> getAllUserComments(Long userId);
}
