package ru.yandex.practicum.event.service;

import com.querydsl.core.BooleanBuilder;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.querydsl.QSort;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.dto.event.EventPublicFilterParamsDto;
import ru.yandex.practicum.dto.event.EventState;
import ru.yandex.practicum.dto.location.LocationDto;
import ru.yandex.practicum.event.model.Event;
import ru.yandex.practicum.event.model.QEvent;
import ru.yandex.practicum.event.repository.EventRepository;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.util.DateTimeUtil;
import ru.yandex.practicum.util.PagingUtil;

import java.util.List;

@Slf4j
@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
public class PublicEventServiceImpl implements PublicEventService {

    EventRepository eventRepository;

    @Override
    public Event getEventById(Long eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException("Такого события не существует: " + eventId));
        return event;
    }

    @Override
    public List<Event> getFilteredEvents(EventPublicFilterParamsDto filters,
                                         int from,
                                         int size,
                                         List<LocationDto> locations,
                                         HttpServletRequest request) {
        QEvent qEvent = QEvent.event;

        BooleanBuilder builder = new BooleanBuilder();

        builder.and(qEvent.state.eq(EventState.PUBLISHED));

        if (filters.getText() != null)
            builder.and(qEvent.annotation.containsIgnoreCase(filters.getText())
                    .or(qEvent.description.containsIgnoreCase(filters.getText()))
                    .or(qEvent.title.containsIgnoreCase(filters.getText())));

        if (filters.getCategories() != null && !filters.getCategories().isEmpty())
            builder.and(qEvent.category.id.in(filters.getCategories()));

        if (filters.getPaid() != null)
            builder.and(qEvent.paid.eq(filters.getPaid()));

        if (filters.getRangeStart() != null && filters.getRangeEnd() == null)
            builder.and(qEvent.eventDate.goe(DateTimeUtil.currentDateTime()));
        else {
            if (filters.getRangeStart() != null)
                builder.and(qEvent.eventDate.goe(filters.getRangeStart()));

            if (filters.getRangeEnd() != null)
                builder.and(qEvent.eventDate.loe(filters.getRangeEnd()));
        }

        if (filters.getLon() != null && filters.getLat() != null)
            builder.and(qEvent.locationId.in(locations.stream().map(LocationDto::getId).toList()));

        PageRequest page = PagingUtil.pageOf(from, size);
        if (filters.getSort() != null && filters.getSort() == EventPublicFilterParamsDto.EventSort.EVENT_DATE)
            page.withSort(new QSort(qEvent.eventDate.desc()));

        return eventRepository.findAll(builder, page).toList();
    }
}
