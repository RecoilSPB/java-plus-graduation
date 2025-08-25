package ru.yandex.practicum.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import ru.yandex.practicum.dto.request.EventRequestCountDto;
import ru.yandex.practicum.dto.request.EventRequestDto;
import ru.yandex.practicum.dto.request.EventRequestStatus;

import java.util.List;

@FeignClient(name = "request-service", path = "/internal/api/requests")
public interface RequestClient {
    @GetMapping("/search")
    List<EventRequestDto> getByStatus(@RequestParam(name = "eventId") Long eventId,
                                      @RequestParam(name = "status") EventRequestStatus status);

    @GetMapping("/search/all")
    List<EventRequestDto> getByIds(@RequestParam(name = "id") List<Long> ids);

    @GetMapping
    List<EventRequestCountDto> getConfirmedCount(@RequestParam(name = "eventId") List<Long> ids);

    @PostMapping
    List<EventRequestDto> updateStatus(
            @RequestParam(name = "status") EventRequestStatus status,
            @RequestBody List<Long> ids);
}