package ru.yandex.practicum.mapper;

import org.mapstruct.Mapper;
import ru.yandex.practicum.dto.StatsDto;
import ru.yandex.practicum.dto.StatsResponseDto;
import ru.yandex.practicum.model.Requests;
import ru.yandex.practicum.model.Response;

@Mapper(componentModel = "spring")
public interface StatsMapper {

    StatsDto toStatsDto(Requests request);

    Requests toRequest(StatsDto requestDto);

    StatsResponseDto toStatsResponseDto(Response stats);
}
