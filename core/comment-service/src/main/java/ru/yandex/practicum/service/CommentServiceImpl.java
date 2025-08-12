package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.client.UserClient;
import ru.yandex.practicum.dto.comment.CommentDto;
import ru.yandex.practicum.dto.comment.GetCommentsAdminRequest;
import ru.yandex.practicum.dto.event.EventState;
import ru.yandex.practicum.dto.user.UserShortDto;
import ru.yandex.practicum.event.model.Event;
import ru.yandex.practicum.event.repository.EventRepository;
import ru.yandex.practicum.exception.ConflictException;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.mapper.CommentMapper;
import ru.yandex.practicum.model.Comment;
import ru.yandex.practicum.repository.CommentRepository;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    final CommentRepository commentRepository;
    final EventRepository eventRepository;
    final CommentMapper commentMapper;
    private final UserClient userClient;

    @Override
    @Transactional
    public CommentDto addComment(final CommentDto commentDto, Long userId, Long eventId) {
        commentDto.setUserId(userId);
        commentDto.setEventId(eventId);

        UserShortDto user = fetchUser(userId);
        Event event = fetchEvent(eventId);
        if (!EventState.PUBLISHED.equals(event.getState())) {
            log.warn("Невозможно добавить комментарий к событию, которое не опубликовано, состояние события = {}",
                    event.getState());
            throw new ConflictException("Невозможно сохранить комментарии для неопубликованного события.");
        }
        Comment comment = commentMapper.mapTo(commentDto, user.getId(), event);
        comment.setCreated(LocalDateTime.now());
        if (user.getId().equals(event.getInitiatorId())) {
            comment.setInitiator(true);
        }
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
        fetchUser(userId);

        if (!comment.getUserId().equals(userId)) {
            throw new ConflictException("Пользователь может удалять только свои комментарии.");
        }

        comment.setContent(commentDto.getContent());
        Comment updated = commentRepository.save(comment);

        return commentMapper.mapToCommentDto(updated);
    }

    @Override
    public List<CommentDto> getAllUserComments(final Long userId) {
        UserShortDto user = fetchUser(userId);
        return commentMapper.mapToCommentDto(commentRepository.findByUserId(user.getId()));
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
        if (!eventRepository.existsById(eventId)) {
            log.warn("Событие с идентификатором {} не существует в базе данных.", eventId);
            throw new NotFoundException("Событие не найдено.");
        }
        final PageRequest page = PageRequest.of(from / size, size);
        return commentRepository.findAllByEventId(eventId, page).getContent();
    }

    private UserShortDto fetchUser(final Long userId) {
        UserShortDto user = userClient.getById(userId);
        if (user == null) {
            log.warn("Пользователь с идентификатором {} не найден.", userId);
            throw new NotFoundException("Пользователь не найден.");
        }
        return user;
    }

    private Event fetchEvent(final Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> {
                    log.warn("Событие с идентификатором {} не найдено.", eventId);
                    return new NotFoundException("Событие не найдено.");
                });
    }

    private Comment fetchComment(final Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> {
                    log.warn("Комментарий с идентификатором {} не найден.", commentId);
                    return new NotFoundException("Комментарий не найден.");
                });
    }
}
