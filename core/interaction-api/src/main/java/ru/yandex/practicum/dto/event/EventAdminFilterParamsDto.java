package ru.yandex.practicum.dto.event;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.DateTimeFormat;
import ru.yandex.practicum.util.DateTimeUtil;
import ru.yandex.practicum.validation.DateTimeRange;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@DateTimeRange(before = "rangeStart", after = "rangeEnd")
public class EventAdminFilterParamsDto {
    List<Long> users;
    List<EventState> states;
    List<Long> categories;
    @DateTimeFormat(pattern = DateTimeUtil.DATE_TIME_FORMAT)
    LocalDateTime rangeStart;
    @DateTimeFormat(pattern = DateTimeUtil.DATE_TIME_FORMAT)
    LocalDateTime rangeEnd;
}