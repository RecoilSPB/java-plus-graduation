package ru.yandex.practicum.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.dto.event.EventFullDto;
import ru.yandex.practicum.dto.event.EventState;
import ru.yandex.practicum.dto.request.EventRequestCountDto;
import ru.yandex.practicum.dto.request.EventRequestDto;
import ru.yandex.practicum.dto.request.EventRequestStatus;
import ru.yandex.practicum.exception.ConflictException;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.exception.ValidationException;
import ru.yandex.practicum.mapper.EventRequestMapper;
import ru.yandex.practicum.model.EventRequest;
import ru.yandex.practicum.repository.RequestRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventRequestServiceImpl implements EventRequestService {

    final EventRequestMapper eventRequestMapper;
    final RequestRepository requestRepository;

    @Override
    @Transactional
    public EventRequestDto addRequest(Long userId, EventFullDto event) {
        if (event.getInitiator().getId().equals(userId)) {
            throw new ConflictException("Создатель события не может подать заявку на участие");
        }
        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new ConflictException("Событие не опубликовано");
        }
        List<EventRequest> requests = requestRepository.findByEventId(event.getId());
        participationLimitIsFull(event);

        for (EventRequest request : requests) {
            if (request.getRequesterId().equals(userId)) {
                throw new ConflictException("Повторная заявка на участие в событии");
            }
        }

        EventRequest newRequest = createNewEventRequest(userId, event);
        return eventRequestMapper.mapRequest(requestRepository.save(newRequest));
    }

    @Override
    public List<EventRequestDto> getUserRequests(Long userId) {
        return requestRepository.findByRequesterId(userId).stream()
                .map(eventRequestMapper::mapRequest)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventRequestDto cancelRequest(Long userId, Long requestId) {
        EventRequest request = requestRepository.findById(requestId).orElseThrow(
                () -> new NotFoundException("Запрос не существует")
        );
        if (!request.getRequesterId().equals(userId)) {
            throw new ValidationException("Создатель заявки не userId=" + userId);
        }
        request.setStatus(EventRequestStatus.CANCELED);
        return eventRequestMapper.mapRequest(requestRepository.save(request));
    }


    @Override
    public List<EventRequestDto> findAllByEventIdAndStatus(Long eventId, EventRequestStatus status) {
        return requestRepository.findAllByEventIdAndStatus(eventId, status)
                .stream()
                .map(eventRequestMapper::mapRequest)
                .toList();
    }

    @Override
    public List<EventRequestDto> getByIds(List<Long> ids) {
        return requestRepository.findAllById(ids)
                .stream()
                .map(eventRequestMapper::mapRequest)
                .toList();
    }

    @Override
    public List<EventRequestCountDto> getConfirmedCount(List<Long> ids) {
        return requestRepository.getCountConfirmed(ids)
                .stream()
                .map(eventRequestMapper::mapRequest)
                .toList();
    }

    @Override
    public List<EventRequestDto> updateStatus(EventRequestStatus status, List<Long> ids) {
        List<EventRequest> requests = requestRepository.findAllById(ids);

        if (status == EventRequestStatus.REJECTED &&
                requests.stream().anyMatch(request -> request.getStatus() == EventRequestStatus.CONFIRMED)) {
            throw new ConflictException("Среди заявок уже есть подтвержденные");
        }

        requests.forEach(request -> request.setStatus(status));
        List<EventRequest> updatedRequests = requestRepository.saveAll(requests);
        return updatedRequests
                .stream()
                .map(eventRequestMapper::mapRequest)
                .toList();
    }

    private EventRequest createNewEventRequest(Long userId, EventFullDto event) {
        EventRequest newRequest = new EventRequest();
        newRequest.setRequesterId(userId);
        newRequest.setCreated(LocalDateTime.now());
        if (event.getParticipantLimit() == 0) {
            newRequest.setStatus(EventRequestStatus.CONFIRMED);
        } else {
            newRequest.setStatus(EventRequestStatus.PENDING);
        }
        newRequest.setEventId(event.getId());
        if (!event.getRequestModeration()) {
            newRequest.setStatus(EventRequestStatus.CONFIRMED);
        }
        return newRequest;
    }

    private void participationLimitIsFull(EventFullDto event) {
        Long confirmedRequestsCounter = requestRepository.countByEventIdAndStatusIn(event.getId(),
                List.of(EventRequestStatus.CONFIRMED));
        if (event.getParticipantLimit() != 0 && event.getParticipantLimit() <= confirmedRequestsCounter) {
            throw new ConflictException("Превышено число заявок на участие");
        }
    }

}
