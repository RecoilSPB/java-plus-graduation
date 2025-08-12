package ru.yandex.practicum.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.util.DateTimeUtil;

import java.util.List;

@Data
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventRequestDto {

    Long id;

    EventRequestStatus status;

    Long event;

    Long requester;

    @JsonFormat(pattern = DateTimeUtil.DATE_TIME_FORMAT)
    String created;

    List<EventRequestDto> confirmedRequests;

    List<EventRequestDto> rejectedRequests;
}
