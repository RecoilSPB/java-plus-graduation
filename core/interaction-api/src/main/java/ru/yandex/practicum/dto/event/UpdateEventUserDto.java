package ru.yandex.practicum.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.dto.location.NewLocationDto;
import ru.yandex.practicum.util.DateTimeUtil;

import java.time.LocalDateTime;


@Getter
@Setter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateEventUserDto {

    Long category;

    @Size(min = 20, max = 2000)
    String annotation;

    @Size(min = 3, max = 120)
    String title;

    @Size(min = 20, max = 7000)
    String description;

    @Enumerated(EnumType.STRING)
    EventStateActionPrivate stateAction;

    @JsonFormat(pattern = DateTimeUtil.DATE_TIME_FORMAT)
    LocalDateTime eventDate;

    NewLocationDto location;

    Boolean paid;

    @PositiveOrZero
    Integer participantLimit;

    Boolean requestModeration;
}
