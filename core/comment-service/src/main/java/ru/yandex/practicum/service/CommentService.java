package ru.yandex.practicum.service;

import ru.yandex.practicum.dto.comment.CommentDto;
import ru.yandex.practicum.dto.comment.GetCommentsAdminRequest;

import java.util.List;

public interface CommentService {

    CommentDto addComment(CommentDto commentDto);

    void delete(Long userId, Long commentId);

    void delete(Long commentId);

    CommentDto updateUserComment(Long userId, Long commentId, CommentDto commentDto);

    List<CommentDto> getAllUserComments(Long userId);

    List<CommentDto> getAllEventComments(GetCommentsAdminRequest param);

    List<CommentDto> getAllEventComments(Long eventId, int from, int size);

}
