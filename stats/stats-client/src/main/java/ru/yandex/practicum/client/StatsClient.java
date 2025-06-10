package ru.yandex.practicum.client;

import ru.yandex.practicum.dto.StatsRequestDto;
import ru.yandex.practicum.dto.StatsRequestParamsDto;
import ru.yandex.practicum.dto.StatsResponseDto;

import java.util.Collection;

public interface StatsClient {

    Collection<StatsResponseDto> getAllStats(StatsRequestParamsDto statsRequestParamsDto);

    void postStats(StatsRequestDto statsRequestDto);
}
