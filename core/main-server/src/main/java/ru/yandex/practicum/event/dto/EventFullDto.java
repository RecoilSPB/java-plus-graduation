package ru.yandex.practicum.event.dto;

import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.category.dto.CategoryDto;
import ru.yandex.practicum.event.model.EventState;
import ru.yandex.practicum.event.model.Location;
import ru.yandex.practicum.user.dto.UserDto;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventFullDto {

    Long id;
    @Size(min = 1, max = 128)
    String title;
    @Size(min = 1, max = 1024)
    String annotation;
    CategoryDto category;
    Boolean paid;
    String eventDate;
    UserDto initiator;
    @Size(min = 1, max = 1024)
    String description;
    Integer participantLimit;
    EventState state;
    String createdOn;
    Location location;
    Boolean requestModeration;
    Long confirmedRequests;
    String publishedOn;
    Integer views;
}
