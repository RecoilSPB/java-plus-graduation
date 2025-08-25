package ru.yandex.practicum.dto.event;


import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.dto.category.CategoryDto;
import ru.yandex.practicum.dto.user.UserDto;
import ru.yandex.practicum.util.DateTimeUtil;

import java.time.LocalDateTime;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventShortDto {

    Long id;

    @Size(min = 1, max = 128)
    String title;

    @Size(min = 1, max = 1024)
    String annotation;

    Long confirmedRequests;

    CategoryDto category;

    UserDto initiator;

    @NotNull
    @JsonFormat(pattern = DateTimeUtil.DATE_TIME_FORMAT)
    LocalDateTime eventDate;

    Boolean paid;

    Long views;
}
