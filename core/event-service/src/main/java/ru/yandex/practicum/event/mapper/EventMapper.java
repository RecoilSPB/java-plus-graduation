package ru.yandex.practicum.event.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.yandex.practicum.category.mapper.CategoryMapper;
import ru.yandex.practicum.category.model.Category;
import ru.yandex.practicum.dto.event.EventFullDto;
import ru.yandex.practicum.dto.event.EventShortDto;
import ru.yandex.practicum.dto.event.EventState;
import ru.yandex.practicum.dto.event.NewEventDto;
import ru.yandex.practicum.dto.user.UserShortDto;
import ru.yandex.practicum.event.model.Event;
import ru.yandex.practicum.util.JsonFormatPattern;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Mapper(componentModel = "spring")
public interface EventMapper {

    @Mapping(target = "confirmedRequests", ignore = true)
    @Mapping(target = "views", ignore = true)
    EventFullDto mapEventToFullDto(Event event);

    @Mapping(target = "confirmedRequests", ignore = true)
    @Mapping(target = "views", ignore = true)
    @Named(value = "EventShortDto")
    @Mapping(target = "initiator", source = "initiator")
    @Mapping(target = "id", source = "event.id")
    EventShortDto mapEventToShortDto(Event event, UserShortDto initiator);

    Event mapNewEventDtoToEvent(NewEventDto newEvent, Category category);
}
