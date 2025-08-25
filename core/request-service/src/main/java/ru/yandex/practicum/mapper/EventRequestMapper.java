package ru.yandex.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.yandex.practicum.dto.request.EventRequestCountDto;
import ru.yandex.practicum.dto.request.EventRequestDto;
import ru.yandex.practicum.model.EventRequest;
import ru.yandex.practicum.model.EventRequestCount;

@Mapper(componentModel = "spring")
public interface EventRequestMapper {

    @Mapping(target = "requester", source = "requesterId")
    @Mapping(target = "event", source = "eventId")
    EventRequestDto mapRequest(EventRequest request);

    EventRequestCountDto mapRequest(EventRequestCount requestCount);
}
