package ru.yandex.practicum.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.dto.location.NewLocationDto;
import ru.yandex.practicum.util.DateTimeUtil;

import java.time.LocalDateTime;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewEventDto {

    @NotBlank
    @Size(min = 20, max = 2000)
    String annotation;

    Long category;

    @Size(min = 3, max = 120)
    String title;

    @NotBlank
    @Size(min = 20)
    String description;

    @JsonFormat(pattern = DateTimeUtil.DATE_TIME_FORMAT)
    LocalDateTime eventDate;

    NewLocationDto location;

    Boolean paid;

    @PositiveOrZero
    Integer participantLimit;

    Boolean requestModeration;
}
