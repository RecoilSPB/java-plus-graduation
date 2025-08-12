package ru.yandex.practicum.event.service;

import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.category.model.Category;
import ru.yandex.practicum.category.repository.CategoryRepository;
import ru.yandex.practicum.dto.event.EventState;
import ru.yandex.practicum.dto.event.EventStateActionPrivate;
import ru.yandex.practicum.dto.event.NewEventDto;
import ru.yandex.practicum.dto.event.UpdateEventUserDto;
import ru.yandex.practicum.dto.location.LocationDto;
import ru.yandex.practicum.event.mapper.EventMapper;
import ru.yandex.practicum.event.model.Event;
import ru.yandex.practicum.event.repository.EventRepository;
import ru.yandex.practicum.exception.ConflictException;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.exception.ValidationException;
import ru.yandex.practicum.exception.WrongDataException;
import ru.yandex.practicum.util.PagingUtil;

import java.time.LocalDateTime;
import java.util.List;


@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PrivateEventServiceImpl implements PrivateEventService {

    final EventRepository eventRepository;
    final CategoryRepository categoryRepository;
    final EventMapper eventMapper;

    private static void validationEventDate(LocalDateTime eventDate) {
        if (LocalDateTime.now().isAfter(eventDate.minusHours(1))) {
            throw new ValidationException("До начала события меньше часа, изменение невозможно");
        }
        if (eventDate.isBefore(LocalDateTime.now())) {
            throw new WrongDataException("Событие уже завершилось");
        }
    }

    @Override
    public Event addEvent(Long userId, NewEventDto eventDto, Long locationId) {
        log.info("Добавление нового события пользователем {}", userId);
        validationEventDate(eventDto.getEventDate());
        Category category = categoryRepository.findById(eventDto.getCategory()).orElseThrow(
                () -> new NotFoundException("Категория не найдена " + eventDto.getCategory())
        );

        Event event = eventMapper.toEvent(eventDto, category, userId, locationId);

        return eventRepository.save(event);
    }

    @Override
    public List<Event> getUserEvents(Long userId, Integer from, Integer size) {
        PageRequest page = PagingUtil.pageOf(from, size).withSort(Sort.by(Sort.Order.desc("eventDate")));
        return eventRepository.findByInitiatorId(userId, page);
    }

    @Override
    @Transactional
    public Event updateEvent(Long userId, Long eventId, UpdateEventUserDto eventUpdateDto, LocationDto location) {
        log.info("Редактирование данных события и его статуса");
        Event event = getEventById(userId, eventId);
        Long locationId = location == null ? event.getLocationId() : location.getId();

        if ((eventUpdateDto.getStateAction().equals(EventStateActionPrivate.CANCEL_REVIEW)
                && event.getState().equals(EventState.PUBLISHED))) {
            throw new ConflictException("Отклонить опубликованное событие невозможно");
        }
        validationEventDate(eventUpdateDto.getEventDate());

        eventMapper.update(event, eventUpdateDto, locationId);
        if (eventUpdateDto.getStateAction() != null) {
            setStateToEvent(eventUpdateDto, event);
        }

        return eventRepository.save(event);
    }

    private void setStateToEvent(UpdateEventUserDto eventUpdateDto, Event event) {
        if (eventUpdateDto.getStateAction().toString()
                .equalsIgnoreCase(EventStateActionPrivate.CANCEL_REVIEW.toString())) {
            event.setState(EventState.CANCELED);
        } else if (eventUpdateDto.getStateAction().toString()
                .equalsIgnoreCase(EventStateActionPrivate.SEND_TO_REVIEW.toString())) {
            event.setState(EventState.PENDING);
        }
    }

    @Override
    public Event getEventById(Long userId, Long eventId) {
        return eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException(String.format("On event operations - " +
                        "Event doesn't exist with id %s or not available for User with id %s: ", eventId, userId)));
    }
}
