package ru.yandex.practicum.request.dto;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.request.model.EventRequest;

import java.time.format.DateTimeFormatter;
import java.util.List;

import static ru.yandex.practicum.utils.JsonFormatPattern.JSON_FORMAT_PATTERN_FOR_TIME;

@Component
@AllArgsConstructor
public class EventRequestMapper {
    public EventRequestDto mapRequest(EventRequest request) {
        EventRequestDto dto = new EventRequestDto();
        dto.setId(request.getId());
        dto.setRequester(request.getRequester().getId());
        dto.setEvent(request.getEvent().getId());
        dto.setCreated(request.getCreated().format(DateTimeFormatter.ofPattern(JSON_FORMAT_PATTERN_FOR_TIME)));
        dto.setStatus(request.getStatus());
        return dto;
    }

    public EventRequestDto mapRequestWithConfirmedAndRejected(List<EventRequestDto> confirmedRequests,
                                                              List<EventRequestDto> rejectedRequests) {
        EventRequestDto result = new EventRequestDto();
        result.setConfirmedRequests(confirmedRequests);
        result.setRejectedRequests(rejectedRequests);
        return result;
    }
}
