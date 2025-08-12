package ru.yandex.practicum.dto.event;

import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.dto.location.LocationDto;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventFullDto extends EventShortDto {

    @Size(min = 1, max = 1024)
    String description;

    Integer participantLimit;

    EventState state;

    String createdOn;

    LocationDto location;

    Boolean requestModeration;

    String publishedOn;
}
