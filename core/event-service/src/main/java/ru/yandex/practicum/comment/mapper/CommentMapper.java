package ru.yandex.practicum.comment.mapper;

import lombok.experimental.UtilityClass;
import org.mapstruct.Mapper;
import ru.yandex.practicum.comment.model.Comment;
import ru.yandex.practicum.dto.comment.CommentDto;
import ru.yandex.practicum.event.model.Event;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    CommentDto mapToCommentDto(final Comment comment);

    List<CommentDto> mapToCommentDto(final List<Comment> comments);

    Comment mapTo(final CommentDto comment, final Long userId, final Event event);
}
