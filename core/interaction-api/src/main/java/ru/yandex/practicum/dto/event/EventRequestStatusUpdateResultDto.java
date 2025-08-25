package ru.yandex.practicum.dto.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.dto.request.EventRequestDto;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventRequestStatusUpdateResultDto {
    private List<EventRequestDto> confirmedRequests;
    private List<EventRequestDto> rejectedRequests;
}