package ru.yandex.practicum.event.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.category.dto.CategoryDto;
import ru.yandex.practicum.user.dto.UserDto;

import java.time.LocalDateTime;

import static ru.yandex.practicum.utils.JsonFormatPattern.JSON_FORMAT_PATTERN_FOR_TIME;

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
    @JsonFormat(pattern = JSON_FORMAT_PATTERN_FOR_TIME)
    LocalDateTime eventDate;

    Boolean paid;
    Integer views;
}
