package ru.yandex.practicum.facade;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.client.EventClient;
import ru.yandex.practicum.client.UserClient;
import ru.yandex.practicum.dto.comment.CommentDto;
import ru.yandex.practicum.dto.comment.GetCommentsAdminRequest;
import ru.yandex.practicum.dto.event.EventFullDto;
import ru.yandex.practicum.dto.event.EventState;
import ru.yandex.practicum.dto.user.UserShortDto;
import ru.yandex.practicum.exception.ConflictException;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.service.CommentService;

import java.util.Collection;
import java.util.Objects;

@Slf4j
@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CommentFacadeImpl implements CommentFacade {

    UserClient userClient;
    EventClient eventClient;

    CommentService commentService;

    @Override
    public Collection<CommentDto> getAllEventComments(GetCommentsAdminRequest getCommentsAdminRequest) {
        Long eventId = getCommentsAdminRequest.getEventId();
        fetchEvent(eventId);
        return commentService.getAllEventComments(getCommentsAdminRequest);
    }

    @Override
    public void delete(Long commentId) {
        commentService.delete(commentId);
    }

    @Override
    public Collection<CommentDto> getAllEventComments(Long eventId, Integer from, Integer size) {
        return commentService.getAllEventComments(eventId, from, size);
    }

    @Override
    public CommentDto addComment(CommentDto commentDto, Long userId, Long eventId) {
        UserShortDto userShortDto = fetchUser(userId);
        EventFullDto eventFullDto = fetchEvent(eventId);

        if (!EventState.PUBLISHED.equals(eventFullDto.getState())) {
            log.warn("Невозможно добавить комментарий к событию, которое не опубликовано, состояние события = {}",
                    eventFullDto.getState());
            throw new ConflictException("Невозможно сохранить комментарии для неопубликованного события.");
        }

        if (!Objects.equals(commentDto.getUserId(), userId)) {
            commentDto.setUserId(userId);
        }
        if (!Objects.equals(commentDto.getEventId(), eventId)) {
            commentDto.setEventId(eventId);
        }
        if (userShortDto.getId().equals(eventFullDto.getInitiator().getId())) {
            commentDto.setInitiator(true);
        }
        return commentService.addComment(commentDto);
    }

    @Override
    public void delete(Long userId, Long commentId) {
        fetchUser(userId);
        commentService.delete(userId, commentId);
    }

    @Override
    public CommentDto updateUserComment(Long userId, Long commentId, CommentDto commentDto) {
        fetchUser(userId);
        return commentService.updateUserComment(userId, commentId, commentDto);
    }

    @Override
    public Collection<CommentDto> getAllUserComments(Long userId) {
        fetchUser(userId);
        return commentService.getAllUserComments(userId);
    }

    private EventFullDto fetchEvent(Long eventId) {
        EventFullDto eventFullDto = eventClient.getById(eventId);
        if (eventFullDto == null) {
            log.warn("Событие с идентификатором {} не существует в базе данных.", eventId);
            throw new NotFoundException("Событие не найдено.");
        }
        return eventFullDto;
    }

    private UserShortDto fetchUser(final Long userId) {
        UserShortDto user = userClient.getById(userId);
        if (user == null) {
            log.warn("Пользователь с идентификатором {} не найден.", userId);
            throw new NotFoundException("Пользователь не найден.");
        }
        return user;
    }
}
