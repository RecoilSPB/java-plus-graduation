package ru.yandex.practicum.event.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.client.StatsClient;
import ru.yandex.practicum.dto.StatsRequestDto;
import ru.yandex.practicum.dto.StatsRequestParamsDto;
import ru.yandex.practicum.event.dto.EventFullDto;
import ru.yandex.practicum.event.dto.EventMapper;
import ru.yandex.practicum.event.dto.EventShortDto;
import ru.yandex.practicum.event.model.Event;
import ru.yandex.practicum.event.model.EventState;
import ru.yandex.practicum.event.repository.EventRepository;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.exception.ValidationException;
import ru.yandex.practicum.request.repository.RequestRepository;
import ru.yandex.practicum.utils.JsonFormatPattern;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventServiceImpl implements EventService {

    final EventRepository eventRepository;
    final RequestRepository requestRepository;

    final StatsClient statsClient;

    @Override
    public EventFullDto getEventById(Long eventId, String uri, String ip) throws NotFoundException {
        statsClient.postStats(new StatsRequestDto("main-server",
                uri,
                ip,
                LocalDateTime.now()));
        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException("Событие с id=" + eventId + " не найдено"));

        if (!event.getState().equals(EventState.PUBLISHED) && !uri.toLowerCase().contains("admin")) {
            throw new NotFoundException("Такого события не существует");
        }
        var confirmed = requestRepository.countByEventAndStatuses(event.getId(), List.of("CONFIRMED"));
        EventFullDto eventFullDto = EventMapper.mapEventToFullDto(event, confirmed);

        List<String> urls = Collections.singletonList(uri);
        LocalDateTime start = LocalDateTime.parse(eventFullDto.getCreatedOn(), JsonFormatPattern.DATE_TIME_FORMATTER);
        LocalDateTime end = LocalDateTime.now();

        StatsRequestParamsDto statsRequestParamsDto = StatsRequestParamsDto.builder()
                .start(start)
                .end(end)
                .uris(urls)
                .unique(true)
                .build();

        var views = statsClient.getAllStats(statsRequestParamsDto).size();
        eventFullDto.setViews(views);
        return eventFullDto;
    }

    @Override
    public List<EventShortDto> getFilteredEvents(String text,
                                                 List<Long> categories,
                                                 Boolean paid,
                                                 String rangeStart,
                                                 String rangeEnd,
                                                 Boolean onlyAvailable,
                                                 String sort,
                                                 Integer from,
                                                 Integer size,
                                                 String uri,
                                                 String ip) throws ValidationException {
        List<Event> events;
        LocalDateTime startDate;
        LocalDateTime endDate;
        boolean sortDate = sort.equals("EVENT_DATE");
        if (sortDate) {
            if (rangeStart == null && rangeEnd == null && categories != null) {
                //events = eventRepository.findAllByCategoryIdPageable(categories, EventState.PUBLISHED, PageRequest.of(from / size, size, Sort.Direction.DESC));
                events = eventRepository.findAllByCategoryIdPageable(categories, EventState.PUBLISHED,
                        PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "e.eventDate")));
            } else {
                if (rangeStart == null) {
                    startDate = LocalDateTime.now();
                } else {
                    startDate = LocalDateTime.parse(rangeStart, JsonFormatPattern.DATE_TIME_FORMATTER);
                }
                if (text == null) {
                    text = "";
                }
                if (rangeEnd == null) {
                    events = eventRepository.findEventsByText("%" + text.toLowerCase() + "%", EventState.PUBLISHED,
                            PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "e.eventDate")));
                } else {
                    endDate = LocalDateTime.parse(rangeEnd, JsonFormatPattern.DATE_TIME_FORMATTER);
                    if (startDate.isAfter(endDate)) {
                        throw new ValidationException("Дата и время начала поиска не должна быть позже даты и времени конца поиска");
                    } else {
                        events = eventRepository.findAllByTextAndDateRange("%" + text.toLowerCase() + "%",
                                startDate,
                                endDate,
                                EventState.PUBLISHED,
                                PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "e.eventDate")));
                    }
                }
            }
        } else {
            if (rangeStart == null) {
                startDate = LocalDateTime.now();
            } else {
                startDate = LocalDateTime.parse(rangeStart, JsonFormatPattern.DATE_TIME_FORMATTER);
            }
            if (rangeEnd == null) {
                endDate = null;
            } else {
                endDate = LocalDateTime.parse(rangeEnd, JsonFormatPattern.DATE_TIME_FORMATTER);
            }
            if (rangeStart != null && rangeEnd != null) {
                if (startDate.isAfter(endDate)) {
                    throw new ValidationException("Дата и время начала поиска не должна быть позже даты и времени конца поиска");
                }
            }
            events = eventRepository.findEventList(text, categories, paid, startDate, endDate, EventState.PUBLISHED);
        }
        statsClient.postStats(new StatsRequestDto("main-server",
                uri,
                ip,
                LocalDateTime.now()));
        if (!sortDate) {
            List<EventShortDto> shortEventDtos = createShortEventDtos(events);
            shortEventDtos.sort(Comparator.comparing(EventShortDto::getViews));
            if (shortEventDtos.size() > from) {
                if (shortEventDtos.size() > from + size) {
                    shortEventDtos = shortEventDtos.subList(from, from + size);
                } else {
                    shortEventDtos = shortEventDtos.subList(from, shortEventDtos.size());
                }
            } else {
                shortEventDtos = Collections.emptyList();
            }
            return shortEventDtos;
        }
        return createShortEventDtos(events);
    }

    List<EventShortDto> createShortEventDtos(List<Event> events) {
        HashMap<Long, Integer> eventIdsWithViewsCounter = new HashMap<>();

        LocalDateTime startTime = events.getFirst().getCreatedOn();
        ArrayList<String> uris = new ArrayList<>();
        for (Event event : events) {
            uris.add("/events/" + event.getId().toString());
            if (startTime.isAfter(event.getCreatedOn())) {
                startTime = event.getCreatedOn();
            }
        }

        StatsRequestParamsDto statsRequestParamsDto = StatsRequestParamsDto.builder()
                .start(startTime)
                .end(LocalDateTime.now())
                .uris(uris)
                .unique(true)
                .build();

        var viewsCounter = statsClient.getAllStats(statsRequestParamsDto);
        for (var statsDto : viewsCounter) {
            String[] split = statsDto.getUri().split("/");
            eventIdsWithViewsCounter.put(Long.parseLong(split[2]), Math.toIntExact(statsDto.getHits()));
        }
        var requests = requestRepository.findByEventIds(new ArrayList<>(eventIdsWithViewsCounter.keySet()));
        return events.stream()
                .map(EventMapper::mapEventToShortDto)
                .peek(dto -> dto.setConfirmedRequests(
                        requests.stream()
                                .filter((request -> request.getEvent().getId().equals(dto.getId())))
                                .count()
                ))
                .peek(dto -> dto.setViews(eventIdsWithViewsCounter.get(dto.getId())))
                .collect(Collectors.toList());
    }
}
