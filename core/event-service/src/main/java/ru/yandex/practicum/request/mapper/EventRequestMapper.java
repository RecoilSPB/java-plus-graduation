package ru.yandex.practicum.request.mapper;

import lombok.AllArgsConstructor;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.dto.request.EventRequestDto;
import ru.yandex.practicum.request.model.EventRequest;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Mapper(componentModel = "spring")
public interface EventRequestMapper {

    EventRequestDto mapRequest(EventRequest request);

    EventRequestDto mapRequestWithConfirmedAndRejected(List<EventRequestDto> confirmedRequests,
                                                              List<EventRequestDto> rejectedRequests);
}
