package ru.yandex.practicum.event.facade;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.client.LocationClient;
import ru.yandex.practicum.client.RequestClient;
import ru.yandex.practicum.client.StatsClient;
import ru.yandex.practicum.client.UserClient;
import ru.yandex.practicum.dto.StatsParamsDto;
import ru.yandex.practicum.dto.StatsResponseDto;
import ru.yandex.practicum.dto.event.*;
import ru.yandex.practicum.dto.location.LocationDto;
import ru.yandex.practicum.dto.location.NewLocationDto;
import ru.yandex.practicum.dto.request.EventRequestCountDto;
import ru.yandex.practicum.dto.request.EventRequestDto;
import ru.yandex.practicum.dto.user.UserShortDto;
import ru.yandex.practicum.event.mapper.EventMapper;
import ru.yandex.practicum.event.model.Event;
import ru.yandex.practicum.event.service.AdminEventService;
import ru.yandex.practicum.event.service.PrivateEventService;
import ru.yandex.practicum.event.service.PublicEventService;
import ru.yandex.practicum.exception.LocationProcessingException;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.util.DateTimeUtil;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventFacadeImpl implements EventFacade {
    private final UserClient userClient;
    private final StatsClient statClient;
    private final LocationClient locationClient;
    private final RequestClient requestClient;

    private final AdminEventService adminEventService;
    private final PublicEventService publicEventService;
    private final PrivateEventService privateEventService;
    private final EventMapper eventMapper;


    @Override
    public EventFullDto addEvent(Long userId, NewEventDto newEventDto) {
        log.debug("Creating new event for user ID: {}", userId);

        // Получаем данные пользователя
        UserShortDto user = getUserById(userId);
        log.trace("Retrieved user: {}", user);

        // Обрабатываем локацию
        LocationDto locationDto = processEventLocation(newEventDto.getLocation());

        // Создаем событие
        Event event = privateEventService.addEvent(
                user.getId(),
                newEventDto,
                locationDto.getId()
        );
        log.info("Created new event with ID: {}", event.getId());

        // Формируем полный DTO ответа
        return eventMapper.toFullDto(event, locationDto, user);
    }

    @Override
    public List<EventShortDto> getEventsByUserId(Long id, int from, int size) {
        UserShortDto user = getUserById(id);
        List<Event> events = privateEventService.getUserEvents(id, from, size);

        List<EventShortDto> eventsDto = events.stream()
                .map(event -> eventMapper.toShortDto(event, user))
                .toList();

        populateWithConfirmedRequests(events, eventsDto);
        populateWithStats(eventsDto);

        return eventsDto;
    }

    @Override
    public EventFullDto getEventById(Long userId, Long eventId) {
        UserShortDto user = getUserById(userId);
        Event event = privateEventService.getEventById(userId, eventId);
        LocationDto location = locationClient.getById(event.getLocationId());

        EventFullDto eventDto = eventMapper.toFullDto(event, location, user);
        populateWithConfirmedRequests(List.of(event), List.of(eventDto));
        populateWithStats(List.of(eventDto));

        return eventDto;
    }

    @Override
    public EventFullDto getEventById(Long eventId) {
        Event event = publicEventService.getEventById(eventId);
        UserShortDto user = getUserById(event.getInitiatorId());
        LocationDto location = locationClient.getById(event.getLocationId());

        EventFullDto eventDto = eventMapper.toFullDto(event, location, user);
        populateWithConfirmedRequests(List.of(event), List.of(eventDto));
        populateWithStats(List.of(eventDto));

        return eventDto;
    }

    @Override
    public EventFullDto updateEvent(Long userId, Long eventId, UpdateEventUserDto updateEventUserDto) {
        log.info("updateEvent: {}", updateEventUserDto);
        LocationDto location = updateEventUserDto.getLocation() == null ? null :
                processEventLocation(updateEventUserDto.getLocation());
        log.info("updateEvent new location : {}", location);
        UserShortDto user = getUserById(userId);

        Event event = privateEventService.updateEvent(userId, eventId, updateEventUserDto, location);
        EventFullDto eventDto = eventMapper.toFullDto(event, location, user);
        populateWithConfirmedRequests(List.of(event), List.of(eventDto));
        populateWithStats(List.of(eventDto));

        return eventDto;
    }

    @Override
    public EventFullDto updateEvent(Long eventId, UpdateEventAdminDto updateEventAdminDto) {
        log.info("update: {}", updateEventAdminDto);
        LocationDto location = updateEventAdminDto.getLocation() == null ? null :
                processEventLocation(updateEventAdminDto.getLocation());
        log.info("update new location : {}", location);
        Event event = adminEventService.updateEvent(eventId, updateEventAdminDto, location);
        UserShortDto user = getUserById(event.getInitiatorId());
        EventFullDto eventDto = eventMapper.toFullDto(event, location, user);
        populateWithConfirmedRequests(List.of(event), List.of(eventDto));
        populateWithStats(List.of(eventDto));

        return eventDto;
    }

    @Override
    public EventFullDto getEventById(Long eventId, HttpServletRequest request) {
        return null;
    }

    @Override
    public List<EventFullDto> getEvents(EventAdminFilterParamsDto filters, int from, int size) {
        return List.of();
    }

    @Override
    public List<EventShortDto> getFilteredEvents(EventPublicFilterParamsDto filters,
                                                 int from,
                                                 int size,
                                                 HttpServletRequest request) {
        return List.of();
    }

    @Override
    public List<EventRequestDto> getEventAllParticipationRequests(Long eventId, Long userId) {
        return List.of();
    }

    @Override
    public EventRequestStatusUpdateResultDto changeEventState(Long userId,
                                                              Long eventId,
                                                              EventRequestStatusUpdateRequestDto requestStatusUpdateRequest) {
        return null;
    }

    @Override
    public List<EventFullDto> getByLocation(Long locationId) {
        return List.of();
    }

    private UserShortDto getUserById(Long userId) {
        UserShortDto user = userClient.getById(userId);
        if (user == null) {
            throw new NotFoundException("Такого пользователя не существует: " + userId);
        }
        return user;
    }

    private LocationDto processEventLocation(NewLocationDto locationRequest) {
        try {
            LocationDto location = locationClient.addOrGetLocation(locationRequest);
            log.trace("Processed location with ID: {}", location.getId());
            return location;
        } catch (Exception e) {
            log.error("Failed to process event location", e);
            throw new LocationProcessingException("Could not process event location", e);
        }
    }

    private void populateWithStats(List<? extends EventShortDto> eventsDto) {
        if (eventsDto.isEmpty()) return;

        Map<String, EventShortDto> uris = eventsDto.stream()
                .collect(Collectors.toMap(e -> String.format("/events/%s", e.getId()), e -> e));

        LocalDateTime currentDateTime = DateTimeUtil.currentDateTime();
        List<StatsResponseDto> stats = statClient.getAllStats(StatsParamsDto.builder()
                .start(currentDateTime.minusDays(1))
                .end(currentDateTime)
                .uris(uris.keySet().stream().toList())
                .unique(true)
                .build()).stream().toList();

        stats.forEach(stat -> Optional.ofNullable(uris.get(stat.getUri()))
                .ifPresent(e -> e.setViews(stat.getHits())));
    }

    private void populateWithConfirmedRequests(List<Event> events, List<? extends EventShortDto> eventsDto) {
        populateWithConfirmedRequests(events, eventsDto, null);
    }

    private void populateWithConfirmedRequests(List<Event> events,
                                               List<? extends EventShortDto> eventsDto,
                                               Boolean filterOnlyAvailable) {
        List<Long> ids = eventsDto.stream()
                .map(EventShortDto::getId)
                .toList();
        Map<Long, EventRequestCountDto> confirmedRequests = requestClient.getConfirmedCount(ids)
                .stream()
                .collect(Collectors.toMap(EventRequestCountDto::getEventId, Function.identity()));
        EventRequestCountDto zeroCount = new EventRequestCountDto(0L, 0L);
        eventsDto.forEach(event ->
                event.setConfirmedRequests(confirmedRequests.getOrDefault(event.getId(), zeroCount).getQuantity()));

        if (filterOnlyAvailable != null && filterOnlyAvailable) {
            eventsDto.removeIf(event -> publicEventService.getEventById(event.getId()).getParticipantLimit() -
                    event.getConfirmedRequests() <= 0);
        }
    }
}