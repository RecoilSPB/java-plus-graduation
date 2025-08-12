package ru.yandex.practicum.client;

import ru.yandex.practicum.dto.StatsDto;
import ru.yandex.practicum.dto.StatsParamsDto;
import ru.yandex.practicum.dto.StatsResponseDto;

import java.util.List;

public interface StatsClient {

    List<StatsResponseDto> getAllStats(StatsParamsDto statsParamsDto);

    void postStats(StatsDto statsDto);
}
