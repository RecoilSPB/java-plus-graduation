package ru.yandex.practicum.dto.compilation;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.dto.event.EventShortDto;

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
