package ru.yandex.practicum.mapper;

import org.mapstruct.Mapper;
import ru.yandex.practicum.dto.comment.CommentDto;
import ru.yandex.practicum.model.Comment;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    CommentDto mapToCommentDto(final Comment comment);

    List<CommentDto> mapToCommentDto(final List<Comment> comments);

    Comment mapTo(final CommentDto comment, final Long userId, final Long eventId);
}
