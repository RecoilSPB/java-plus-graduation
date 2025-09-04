package ru.yandex.practicum.event.facade;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.client.LocationClient;
import ru.yandex.practicum.client.RequestClient;
import ru.yandex.practicum.client.StatsClient;
import ru.yandex.practicum.client.UserClient;
import ru.yandex.practicum.dto.event.*;
import ru.yandex.practicum.dto.location.LocationDto;
import ru.yandex.practicum.dto.location.NewLocationDto;
import ru.yandex.practicum.dto.request.EventRequestCountDto;
import ru.yandex.practicum.dto.request.EventRequestDto;
import ru.yandex.practicum.dto.request.EventRequestStatus;
import ru.yandex.practicum.dto.user.UserShortDto;
import ru.yandex.practicum.event.mapper.EventMapper;
import ru.yandex.practicum.event.model.Event;
import ru.yandex.practicum.event.service.AdminEventService;
import ru.yandex.practicum.event.service.PrivateEventService;
import ru.yandex.practicum.event.service.PublicEventService;
import ru.yandex.practicum.exception.ConflictException;
import ru.yandex.practicum.exception.LocationProcessingException;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.grpc.stats.action.ActionTypeProto;

import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class EventFacadeImpl implements EventFacade {
    static String APP_NAME_FOR_STAT = "event-service";
    UserClient userClient;
    StatsClient statClient;
    LocationClient locationClient;
    RequestClient requestClient;
    AdminEventService adminEventService;
    PublicEventService publicEventService;
    PrivateEventService privateEventService;
    EventMapper eventMapper;

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
    public EventFullDto getEventById(Long eventId, Long userId, HttpServletRequest request) {
        Event event = publicEventService.getEventById(eventId);
        if (!EventState.PUBLISHED.equals(event.getState()))
            throw new NotFoundException("On Event public get - Event isn't published with id: " + eventId);
        UserShortDto user = getUserById(event.getInitiatorId());
        LocationDto location = locationClient.getById(event.getLocationId());

        EventFullDto eventDto = eventMapper.toFullDto(event, location, user);
        populateWithConfirmedRequests(List.of(event), List.of(eventDto));
        populateWithStats(List.of(eventDto));

        statClient.registerUserAction(event.getId(), userId, ActionTypeProto.ACTION_VIEW, Instant.now());
        return eventDto;
    }

    @Override
    public List<EventFullDto> getEvents(EventAdminFilterParamsDto filters, int from, int size) {
        List<Event> events = adminEventService.getEvents(filters, from, size);

        List<EventFullDto> eventsDto = new ArrayList<>(eventMapper.toFullDto(events));

        populateWithConfirmedRequests(events, eventsDto);
        populateWithStats(eventsDto);

        return eventsDto;
    }

    @Override
    public List<EventShortDto> getFilteredEvents(EventPublicFilterParamsDto filters,
                                                 int from,
                                                 int size,
                                                 HttpServletRequest request) {
        List<LocationDto> locations = getLocationsByRadius(filters.getLat(), filters.getLon(), filters.getRadius());
        List<Event> events = publicEventService.getFilteredEvents(filters, from, size, locations, request);

        List<EventShortDto> eventsDto = events.stream()
                .map(event -> eventMapper.toShortDto(event, null))
                .collect(Collectors.toCollection(ArrayList::new));

        populateWithConfirmedRequests(events, eventsDto, true);
        populateWithStats(eventsDto);

        if (filters.getSort() != null && EventPublicFilterParamsDto.EventSort.VIEWS.equals(filters.getSort())) {
            eventsDto.sort(Comparator.comparing(EventShortDto::getRating,
                    Comparator.nullsLast(Comparator.reverseOrder())));
        }
        return eventsDto;
    }

    @Override
    public List<EventRequestDto> getEventAllParticipationRequests(Long eventId, Long userId) {
        Event event = privateEventService.checkAndGetEventByIdAndInitiatorId(eventId, userId);
        return requestClient.getByStatus(event.getId(), EventRequestStatus.PENDING);
    }

    @Override
    public EventRequestStatusUpdateResultDto changeEventState(Long userId,
                                                              Long eventId,
                                                              EventRequestStatusUpdateRequestDto requestStatusUpdateRequest) {
        Event event = privateEventService.checkAndGetEventByIdAndInitiatorId(eventId, userId);
        int participantsLimit = event.getParticipantLimit();

        List<EventRequestDto> confirmedRequests = requestClient.getByStatus(eventId,
                EventRequestStatus.CONFIRMED);
        log.info("confirmedRequests: {}", confirmedRequests);

        List<EventRequestDto> requestToChangeStatus = requestClient.getByIds(requestStatusUpdateRequest.getRequestIds());
        List<Long> idsToChangeStatus = requestToChangeStatus.stream()
                .map(EventRequestDto::getId)
                .toList();
        log.info("idsToChangeStatus: {}", idsToChangeStatus);
        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            log.info("Заявки подтверждать не требуется");
            return null;
        }

        log.info("Заявки:  Лимит: {}, подтвержденных заявок {}, запрошенных заявок {}, разница между ними: {}", participantsLimit,
                confirmedRequests.size(), requestStatusUpdateRequest.getRequestIds().size(), (participantsLimit
                        - confirmedRequests.size() - requestStatusUpdateRequest.getRequestIds().size()));

        if (requestStatusUpdateRequest.getStatus().equals(EventRequestStatus.CONFIRMED)) {
            log.info("меняем статус заявок для статуса: {}", EventRequestStatus.CONFIRMED);
            if ((participantsLimit - (confirmedRequests.size()) - requestStatusUpdateRequest.getRequestIds().size()) >= 0) {
                List<EventRequestDto> requestUpdated = requestClient.updateStatus(
                        EventRequestStatus.CONFIRMED, idsToChangeStatus);
                return new EventRequestStatusUpdateResultDto(requestUpdated, null);
            } else {
                throw new ConflictException("слишком много участников. Лимит: " + participantsLimit +
                        ", уже подтвержденных заявок: " + confirmedRequests.size() + ", а заявок на одобрение: " +
                        idsToChangeStatus.size() +
                        ". Разница между ними: " + (participantsLimit - confirmedRequests.size() -
                        idsToChangeStatus.size()));
            }
        } else if (requestStatusUpdateRequest.getStatus().equals(EventRequestStatus.REJECTED)) {
            log.info("меняем статус заявок для статуса: {}", EventRequestStatus.REJECTED);

            for (EventRequestDto request : requestToChangeStatus) {
                if (request.getStatus() == EventRequestStatus.CONFIRMED) {
                    throw new ConflictException("Заявка " + request.getStatus() + " уже подтверждена.");
                }
            }

            List<EventRequestDto> requestUpdated = requestClient.updateStatus(
                    EventRequestStatus.REJECTED, idsToChangeStatus);
            return new EventRequestStatusUpdateResultDto(null, requestUpdated);
        }
        return null;
    }

    @Override
    public List<EventFullDto> getByLocation(Long locationId) {
        return adminEventService.getByLocation(locationId)
                .stream()
                .map(eventMapper::toFullDto)
                .toList();
    }

    @Override
    public Stream<RecommendedEventDto> getRecommendations(Long userId, int limit) {
        return statClient.getRecommendationsForUser(userId, limit)
                .map(eventMapper::map);
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
        if (eventsDto.isEmpty()) {
            return;
        }

        List<Long> eventIds = eventsDto.stream()
                .map(EventShortDto::getId).toList();

        Map<Long, Double> ratedEvents = statClient.getEventsInteractionsCount(eventIds)
                .map(eventMapper::map)
                .collect(Collectors.toMap(RecommendedEventDto::getEventId, RecommendedEventDto::getScore));

        eventsDto.forEach(event -> Optional.ofNullable(ratedEvents.get(event.getId()))
                .ifPresent(event::setRating));
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

    private List<LocationDto> getLocationsByRadius(Float lat, Float lon, Float radius) {
        if (lat == null || lon == null) {
            return Collections.emptyList();
        }

        return locationClient.getByRadius(lat, lon, radius);
    }
}