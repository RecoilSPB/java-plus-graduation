package ru.yandex.practicum.service;

import ru.yandex.practicum.dto.location.LocationDto;
import ru.yandex.practicum.dto.location.NewLocationDto;
import ru.yandex.practicum.dto.location.AdminLocationUpdateDto;
import ru.yandex.practicum.exception.ConflictException;
import ru.yandex.practicum.exception.DataRetrievalException;
import ru.yandex.practicum.exception.NotFoundException;

import java.util.List;

public interface LocationService {

    List<LocationDto> getLocations(Integer from, Integer size);

    LocationDto getById(Long locationId) throws NotFoundException;

    LocationDto addLocation(NewLocationDto newLocationDto);

    LocationDto updateLocation(Long locationId, AdminLocationUpdateDto adminLocationUpdateDto) throws NotFoundException;

    LocationDto addOrGetLocation(NewLocationDto newLocationDto);

    List<LocationDto> getByRadius(Double lat, Double lon, Double radius) throws DataRetrievalException;

    void delete(Long locationId) throws ConflictException;
}
