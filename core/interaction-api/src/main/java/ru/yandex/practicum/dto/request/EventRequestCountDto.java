package ru.yandex.practicum.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EventRequestCountDto {
    Long eventId;
    Long quantity;
}