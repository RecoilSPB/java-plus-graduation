package ru.yandex.practicum.request.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventRequestDto {

    Long id;

    String status;

    Long event;

    Long requester;

    String created;

    List<EventRequestDto> confirmedRequests;

    List<EventRequestDto> rejectedRequests;
}
