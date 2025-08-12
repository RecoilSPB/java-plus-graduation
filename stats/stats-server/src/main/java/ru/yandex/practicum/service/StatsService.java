package ru.yandex.practicum.service;

import ru.yandex.practicum.dto.StatsDto;
import ru.yandex.practicum.dto.StatsResponseDto;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsService {
    StatsDto save(StatsDto requestDto);

    List<StatsResponseDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique);

}
