package ru.yandex.practicum.compilation.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.event.dto.EventShortDto;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class ResponseCompilationDto {
    Long id;
    String title;
    Boolean pinned;
    List<EventShortDto> events;
}
