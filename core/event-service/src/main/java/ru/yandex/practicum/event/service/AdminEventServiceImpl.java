package ru.yandex.practicum.event.service;

import com.querydsl.core.BooleanBuilder;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.querydsl.QSort;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.category.model.Category;
import ru.yandex.practicum.category.repository.CategoryRepository;
import ru.yandex.practicum.dto.event.EventAdminFilterParamsDto;
import ru.yandex.practicum.dto.event.EventState;
import ru.yandex.practicum.dto.event.EventStateActionAdmin;
import ru.yandex.practicum.dto.event.UpdateEventAdminDto;
import ru.yandex.practicum.dto.location.LocationDto;
import ru.yandex.practicum.event.mapper.EventMapper;
import ru.yandex.practicum.event.model.Event;
import ru.yandex.practicum.event.model.QEvent;
import ru.yandex.practicum.event.repository.EventRepository;
import ru.yandex.practicum.exception.ConflictException;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.exception.ValidationException;
import ru.yandex.practicum.util.DateTimeUtil;
import ru.yandex.practicum.util.PagingUtil;

import java.time.LocalDateTime;
import java.util.List;


@Slf4j
@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdminEventServiceImpl implements AdminEventService {
    EventRepository eventRepository;
    CategoryRepository categoryRepository;
    EventMapper eventMapper;

    private static void calculateNewEventState(Event event, EventStateActionAdmin eventStateActionAdmin) {
        if (EventStateActionAdmin.REJECT_EVENT.equals(eventStateActionAdmin)) {
            if (EventState.PUBLISHED.equals(event.getState())) {
                throw new ConflictException("Отклонить опубликованное событие невозможно");
            }
            event.setState(EventState.CANCELED);
        } else if (EventStateActionAdmin.PUBLISH_EVENT.equals(eventStateActionAdmin)) {
            if (LocalDateTime.now().isAfter(event.getEventDate().minusHours(2))) {
                throw new ConflictException("До начала события меньше часа, изменение события невозможно");
            }
            if (!EventState.PENDING.equals(event.getState())) {
                throw new ConflictException("Событие не в состоянии \"Ожидание публикации\", изменение события невозможно");
            }
            LocalDateTime currentDateTime = DateTimeUtil.currentDateTime();
            event.setPublishedOn(currentDateTime);
            event.setState(EventState.PUBLISHED);
        }
    }

    @Override
    public List<Event> getEvents(EventAdminFilterParamsDto filters, int from, int size) {

        QEvent event = QEvent.event;

        BooleanBuilder builder = new BooleanBuilder();

        if (filters.getUsers() != null && !filters.getUsers().isEmpty()) {
            List<Long> users = filters.getUsers().stream().filter(userId -> userId > 0).toList();
            if (!users.isEmpty())
                builder.and(event.initiatorId.in(users));
        }

        if (filters.getStates() != null && !filters.getStates().isEmpty())
            builder.and(event.state.in(filters.getStates()));

        if (filters.getCategories() != null && !filters.getCategories().isEmpty()) {
            List<Long> categories = filters.getCategories().stream().filter(categoryId -> categoryId > 0).toList();
            if (!categories.isEmpty())
                builder.and(event.category.id.in(categories));
        }

        if (filters.getRangeStart() != null)
            builder.and(event.eventDate.goe(filters.getRangeStart()));

        if (filters.getRangeEnd() != null)
            builder.and(event.eventDate.loe(filters.getRangeEnd()));

        return eventRepository.findAll(builder,
                PagingUtil.pageOf(from, size).withSort(new QSort(event.createdOn.desc()))).toList();
    }

    @Override
    public Event updateEvent(Long eventId, UpdateEventAdminDto updateEventAdminDto, LocationDto location) {
        log.info("Редактирование данных события и его статуса");
        Event event = getEventById(eventId);
        if (event.getEventDate().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Событие уже завершилось");
        }
        calculateNewEventState(event, updateEventAdminDto.getStateAction());

        Long locationId = location == null ? event.getLocationId() : location.getId();
        Category category = null;
        if (updateEventAdminDto.getCategory() != null) {
            category = categoryRepository.findById(updateEventAdminDto.getCategory())
                    .orElseThrow(() -> new NotFoundException("On Event admin update - Category doesn't exist with id: " +
                            updateEventAdminDto.getCategory()));
        }

        event = eventMapper.update(event, updateEventAdminDto, category, locationId);

        event = eventRepository.save(event);
        log.info("Event is updated by admin: {}", event);

        return event;
    }

    private Event getEventById(Long eventId) {
        return eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException("Событие " + eventId + " не найдено"));
    }

}
