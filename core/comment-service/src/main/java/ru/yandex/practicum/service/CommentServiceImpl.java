package ru.yandex.practicum.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.dto.comment.CommentDto;
import ru.yandex.practicum.dto.comment.GetCommentsAdminRequest;
import ru.yandex.practicum.exception.ConflictException;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.mapper.CommentMapper;
import ru.yandex.practicum.model.Comment;
import ru.yandex.practicum.repository.CommentRepository;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    CommentRepository commentRepository;
    CommentMapper commentMapper;

    @Override
    @Transactional
    public CommentDto addComment(final CommentDto commentDto) {
        Comment comment = commentMapper.mapTo(commentDto);
        comment.setCreated(LocalDateTime.now());
        Comment savedComment = commentRepository.save(comment);
        return commentMapper.mapToCommentDto(savedComment);

    }

    @Override
    @Transactional
    public void delete(final Long userId, final Long commentId) {
        Comment comment = fetchComment(commentId);

        if (!comment.getUserId().equals(userId)) {
            throw new ConflictException("Пользователь может удалять только свои комментарии.");
        }
        commentRepository.delete(comment);
    }

    @Override
    @Transactional
    public void delete(final Long commentId) {
        Comment comment = fetchComment(commentId);
        commentRepository.delete(comment);
    }

    @Override
    @Transactional
    public CommentDto updateUserComment(final Long userId, final Long commentId,
                                        final CommentDto commentDto) {
        Comment comment = fetchComment(commentId);
        if (!comment.getUserId().equals(userId)) {
            throw new ConflictException("Пользователь может обновлять только свои комментарии.");
        }

        comment.setContent(commentDto.getContent());
        Comment updated = commentRepository.save(comment);

        return commentMapper.mapToCommentDto(updated);
    }

    @Override
    public List<CommentDto> getAllUserComments(final Long userId) {
        List<Comment> comments = commentRepository.findByUserId(userId);
        return commentMapper.mapToCommentDto(comments);
    }

    @Override
    public List<CommentDto> getAllEventComments(final GetCommentsAdminRequest param) {
        final List<Comment> comments =
                getEventComments(param.getEventId(), param.getFrom(), param.getSize());
        return commentMapper.mapToCommentDto(comments);
    }

    @Override
    public List<CommentDto> getAllEventComments(final Long eventId, final int from, final int size) {
        return commentMapper.mapToCommentDto(getEventComments(eventId, from, size));
    }

    private List<Comment> getEventComments(final Long eventId, final int from, final int size) {
        final PageRequest page = PageRequest.of(from / size, size);
        return commentRepository.findAllByEventId(eventId, page).getContent();
    }

    private Comment fetchComment(final Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> {
                    log.warn("Комментарий с идентификатором {} не найден.", commentId);
                    return new NotFoundException("Комментарий не найден.");
                });
    }
}
