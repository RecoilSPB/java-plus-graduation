package ru.yandex.practicum.mapper;

import ru.yandex.practicum.dto.StatsRequestDto;
import ru.yandex.practicum.dto.StatsResponseDto;
import ru.yandex.practicum.model.Requests;
import ru.yandex.practicum.model.Response;

public class Mapper {
    public static StatsRequestDto toRequestDto(Requests request) {
        StatsRequestDto statsDto = new StatsRequestDto();
        statsDto.setIp(request.getIp());
        statsDto.setApp(request.getApplication());
        statsDto.setUri(request.getUri());
        statsDto.setTimestamp(request.getMoment());
        return statsDto;
    }

    public static Requests toRequest(StatsRequestDto requestDto) {
        Requests request = new Requests();
        request.setIp(requestDto.getIp());
        request.setApplication(requestDto.getApp());
        request.setUri(requestDto.getUri());
        request.setMoment(requestDto.getTimestamp());
        return request;
    }

    public static StatsResponseDto toResponseDto(Response stats) {
        StatsResponseDto statsDto = new StatsResponseDto();
        statsDto.setApp(stats.getApplication());
        statsDto.setHits(stats.getTotal());
        statsDto.setUri(stats.getUri());
        return statsDto;
    }
}
