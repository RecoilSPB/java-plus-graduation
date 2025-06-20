package ru.yandex.practicum.event.dto;

import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.event.model.Location;

@Getter
@Setter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateEventAdminRequest {

    Long category;

    @Size(min = 3, max = 120)
    String title;

    @Size(min = 20, max = 2000)
    String annotation;

    @Size(min = 20, max = 7000)
    String description;

    String stateAction;
    String eventDate;

    Location location;
    Boolean paid;
    Integer participantLimit;
    Boolean requestModeration;
}
