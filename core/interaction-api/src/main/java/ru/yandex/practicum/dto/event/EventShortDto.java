package ru.yandex.practicum.dto.event;


import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.dto.category.CategoryDto;
import ru.yandex.practicum.dto.user.UserDto;
import ru.yandex.practicum.util.JsonFormatPattern;

import java.time.LocalDateTime;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventShortDto {

    Long id;
    String title;
    String annotation;

    Long confirmedRequests;
    CategoryDto category;
    UserDto initiator;

    @NotNull
    @JsonFormat(pattern = JsonFormatPattern.JSON_FORMAT_PATTERN_FOR_TIME)
    LocalDateTime eventDate;

    Boolean paid;
    Integer views;
}
