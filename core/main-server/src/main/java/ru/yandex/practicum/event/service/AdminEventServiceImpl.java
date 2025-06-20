package ru.yandex.practicum.event.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.category.model.Category;
import ru.yandex.practicum.category.repository.CategoryRepository;
import ru.yandex.practicum.client.StatsClientImpl;
import ru.yandex.practicum.dto.StatsRequestParamsDto;
import ru.yandex.practicum.event.dto.EventFullDto;
import ru.yandex.practicum.event.dto.EventMapper;
import ru.yandex.practicum.event.dto.UpdateEventAdminRequest;
import ru.yandex.practicum.event.model.Event;
import ru.yandex.practicum.event.model.EventState;
import ru.yandex.practicum.event.model.StateAction;
import ru.yandex.practicum.event.repository.EventRepository;
import ru.yandex.practicum.event.repository.LocationRepository;
import ru.yandex.practicum.exception.ConflictException;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.exception.ValidationException;
import ru.yandex.practicum.exception.WrongDataException;
import ru.yandex.practicum.request.model.EventRequest;
import ru.yandex.practicum.request.repository.RequestRepository;
import ru.yandex.practicum.utils.JsonFormatPattern;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
public class AdminEventServiceImpl implements AdminEventService {
    final EventRepository eventRepository;
    final RequestRepository requestRepository;
    final CategoryRepository categoryRepository;
    final LocationRepository locationRepository;

    final StatsClientImpl statsClient;

    private static StatsRequestParamsDto getStatsRequestParamsDto(LocalDateTime start, Optional<LocalDateTime> end, ArrayList<String> urls) {
        return StatsRequestParamsDto.builder()
                .start(start)
                .end(end.orElse(LocalDateTime.now()))
                .uris(urls)
                .unique(true)
                .build();
    }

    @Override
    public List<EventFullDto> getEvents(List<Long> users, List<String> states, List<Long> categories, LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from, Integer size) throws ValidationException {

        List<EventFullDto> eventDtos;
        List<EventState> eventStateList;

        if (rangeStart != null && rangeEnd != null) {
            if (rangeStart.isAfter(rangeEnd)) {
                throw new ValidationException("Время начала поиска позже времени конца поиска");
            }
        }

        if ((states == null) || (states.isEmpty())) {
            eventStateList = Arrays.stream(EventState.values()).collect(Collectors.toList());
        } else {
            eventStateList = states.stream().map(EventState::valueOf).collect(Collectors.toList());
        }

        if (users == null && categories == null) {
            List<Event> allEventsWithDates = new ArrayList<>(eventRepository.findAll(PageRequest.of(from / size, size)).getContent());
            List<EventRequest> requestsByEventIds = requestRepository.findByEventIds(allEventsWithDates.stream()
                    .mapToLong(Event::getId).boxed().collect(Collectors.toList()));
            eventDtos = allEventsWithDates.stream()
                    .map(e -> EventMapper.mapEventToFullDto(e,
                            requestsByEventIds.stream()
                                    .filter(r -> r.getEvent().getId().equals(e.getId()))
                                    .count()))
                    .toList();
        } else {
            List<Event> allEventsWithDates = eventRepository.findAllEventsWithDates(users,
                    eventStateList, categories, rangeStart, rangeEnd,
                    PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "e.eventDate")));

            List<EventRequest> requestsByEventIds = requestRepository.findByEventIds(allEventsWithDates.stream()
                    .mapToLong(Event::getId).boxed().collect(Collectors.toList()));
            eventDtos = allEventsWithDates.stream()
                    .map(e -> EventMapper.mapEventToFullDto(e,
                            requestsByEventIds.stream()
                                    .filter(r -> r.getEvent().getId().equals(e.getId()))
                                    .count()))
                    .toList();
        }

        if (!eventDtos.isEmpty()) {
            HashMap<Long, Integer> eventIdsWithViewsCounter = new HashMap<>();
            LocalDateTime startTime = LocalDateTime.parse(eventDtos.getFirst().getCreatedOn().replace(" ", "T"));
            ArrayList<String> uris = new ArrayList<>();
            for (EventFullDto dto : eventDtos) {
                eventIdsWithViewsCounter.put(dto.getId(), 0);
                uris.add("/events/" + dto.getId().toString());
                if (startTime.isAfter(LocalDateTime.parse(dto.getCreatedOn().replace(" ", "T")))) {
                    startTime = LocalDateTime.parse(dto.getCreatedOn().replace(" ", "T"));
                }
            }


            StatsRequestParamsDto statsRequestParamsDto = getStatsRequestParamsDto(startTime, Optional.empty(), uris);

            var viewsCounter = statsClient.getAllStats(statsRequestParamsDto);
            for (var statsDto : viewsCounter) {
                String[] split = statsDto.getUri().split("/");
                eventIdsWithViewsCounter.put(Long.parseLong(split[2]), Math.toIntExact(statsDto.getHits()));
            }
            ArrayList<Long> longs = new ArrayList<>(eventIdsWithViewsCounter.keySet());
            List<EventRequest> requests = requestRepository.findByEventIdsAndStatus(longs, "CONFIRMED");
            return eventDtos.stream()
                    .peek(dto -> dto.setConfirmedRequests(
                            requests.stream()
                                    .filter((request -> request.getEvent().getId().equals(dto.getId())))
                                    .count()
                    ))
                    .peek(dto -> dto.setViews(eventIdsWithViewsCounter.get(dto.getId())))
                    .collect(Collectors.toList());
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public EventFullDto updateEvent(Long eventId, UpdateEventAdminRequest updateRequest) throws ConflictException, ValidationException, NotFoundException, WrongDataException {
        log.info("Редактирование данных события и его статуса");
        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException("Событие не существует " + eventId));

        if (LocalDateTime.now().isAfter(event.getEventDate().minusHours(2))) {
            throw new ConflictException("До начала события меньше часа, изменение события невозможно");
        }
        if (!event.getState().equals(EventState.PENDING)) {
            throw new ConflictException("Событие не в состоянии \"Ожидание публикации\", изменение события невозможно");
        }
        if ((!StateAction.REJECT_EVENT.toString().equals(updateRequest.getStateAction())
                && event.getState().equals(EventState.PUBLISHED))) {
            throw new ConflictException("Отклонить опубликованное событие невозможно");
        }
        updateEventWithAdminRequest(event, updateRequest);
        if (event.getEventDate().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Событие уже завершилось");
        }
        saveLocation(event);
        event = eventRepository.save(event);
        EventFullDto eventFullDto = getEventFullDto(event);
        return getViewsCounter(eventFullDto);
    }

    EventFullDto getEventFullDto(Event event) {
        Long confirmed = requestRepository.countByEventAndStatuses(event.getId(), List.of("CONFIRMED"));
        return EventMapper.mapEventToFullDto(event, confirmed);
    }

    Event getEventById(Long eventId) throws NotFoundException {
        return eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException("Событие " + eventId + " не найдено"));
    }

    void updateEventWithAdminRequest(Event event, UpdateEventAdminRequest updateRequest) throws NotFoundException, WrongDataException {
        if (updateRequest.getAnnotation() != null) {
            event.setAnnotation(updateRequest.getAnnotation());
        }
        if (updateRequest.getCategory() != null) {
            Category category = categoryRepository.findById(updateRequest.getCategory()).orElseThrow(
                    () -> new NotFoundException("Категория не найдена " + updateRequest.getCategory()));
            event.setCategory(category);
        }
        if (updateRequest.getDescription() != null) {
            event.setDescription(updateRequest.getDescription());
        }
        if (updateRequest.getEventDate() != null) {
            event.setEventDate(LocalDateTime.parse(updateRequest.getEventDate(), JsonFormatPattern.DATE_TIME_FORMATTER));
        }
        if (updateRequest.getLocation() != null) {
            event.setLocation(updateRequest.getLocation());
        }
        if (updateRequest.getPaid() != null) {
            event.setPaid(updateRequest.getPaid());
        }
        if (updateRequest.getParticipantLimit() != null) {
            event.setParticipantLimit(updateRequest.getParticipantLimit());
        }
        if (updateRequest.getRequestModeration() != null) {
            event.setRequestModeration(updateRequest.getRequestModeration());
        }
        if (updateRequest.getStateAction() != null) {
            switch (updateRequest.getStateAction().toUpperCase()) {
                case "PUBLISH_EVENT":
                    event.setState(EventState.PUBLISHED);
                    event.setPublishedOn(LocalDateTime.now());
                    break;
                case "REJECT_EVENT":
                    event.setState(EventState.CANCELED);
                    break;
                default:
                    throw new WrongDataException("Неверное состояние события, не удалось обновить");
            }
        }
        if (updateRequest.getTitle() != null) {
            event.setTitle(updateRequest.getTitle());
        }
    }

    void saveLocation(Event event) {
        event.setLocation(locationRepository.save(event.getLocation()));
        log.info("Локация сохранена {}", event.getLocation().getId());
    }

    EventFullDto getViewsCounter(EventFullDto eventFullDto) {
        ArrayList<String> urls = new ArrayList<>(List.of("/events/" + eventFullDto.getId()));
        LocalDateTime start = LocalDateTime.parse(eventFullDto.getCreatedOn(), JsonFormatPattern.DATE_TIME_FORMATTER);
        LocalDateTime end = LocalDateTime.now();

        StatsRequestParamsDto statsRequestParamsDto = getStatsRequestParamsDto(start, Optional.of(end), urls);

        Integer views = statsClient.getAllStats(statsRequestParamsDto).size();
        eventFullDto.setViews(views);
        return eventFullDto;
    }

}
