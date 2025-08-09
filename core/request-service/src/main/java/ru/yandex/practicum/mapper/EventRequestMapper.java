package ru.yandex.practicum.mapper;

import lombok.AllArgsConstructor;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.dto.request.EventRequestDto;
import ru.yandex.practicum.model.EventRequest;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EventRequestMapper {

    EventRequestDto mapRequest(EventRequest request);

    EventRequestDto mapRequestWithConfirmedAndRejected(List<EventRequestDto> confirmedRequests,
                                                              List<EventRequestDto> rejectedRequests);
}
