package ru.yandex.practicum.event.mapper;

import org.mapstruct.*;
import ru.yandex.practicum.category.model.Category;
import ru.yandex.practicum.dto.event.*;
import ru.yandex.practicum.dto.location.LocationDto;
import ru.yandex.practicum.dto.user.UserShortDto;
import ru.yandex.practicum.event.model.Event;
import ru.yandex.practicum.grpc.stats.request.RecommendedEventProto;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EventMapper {

    @Named(value = "EventShortDto")
    @Mapping(target = "confirmedRequests", ignore = true)
    @Mapping(target = "rating", ignore = true)
    @Mapping(target = "initiator", source = "initiator")
    @Mapping(target = "id", source = "event.id")
    EventShortDto toShortDto(Event event, UserShortDto initiator);

    @Mapping(target = "confirmedRequests", ignore = true)
    @Mapping(target = "rating", ignore = true)
    @Mapping(target = "id", source = "event.id")
    @Mapping(target = "location", source = "location")
    @Mapping(target = "initiator", source = "initiator")
    EventFullDto toFullDto(Event event, LocationDto location, UserShortDto initiator);

    @Mapping(target = "confirmedRequests", ignore = true)
    @Mapping(target = "rating", ignore = true)
    EventFullDto toFullDto(Event event);

    List<EventFullDto> toFullDto(Iterable<Event> event);

    List<EventShortDto> toEventShortDtoList(Iterable<Event> events);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", source = "category")
    @Mapping(target = "initiatorId", source = "userId")
    @Mapping(target = "locationId", source = "locationId")
    @Mapping(target = "participantLimit", defaultValue = "0")
    @Mapping(target = "paid", defaultValue = "false")
    @Mapping(target = "requestModeration", defaultValue = "true")
    @Mapping(target = "state", ignore = true)
    @Mapping(target = "publishedOn", ignore = true)
    Event toEvent(NewEventDto newEventDto, Category category, Long userId, Long locationId);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "initiatorId", ignore = true)
    @Mapping(target = "locationId", source = "locationId")
    @Mapping(target = "state", ignore = true)
    @Mapping(target = "publishedOn", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Event update(@MappingTarget Event event, UpdateEventUserDto eventUpdateDto, Long locationId);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", source = "category")
    @Mapping(target = "initiatorId", ignore = true)
    @Mapping(target = "locationId", source = "locationId")
    @Mapping(target = "state", ignore = true)
    @Mapping(target = "publishedOn", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Event update(@MappingTarget Event event, UpdateEventAdminDto eventUpdateDto, Category category, Long locationId);

    RecommendedEventDto map(RecommendedEventProto proto);
}
