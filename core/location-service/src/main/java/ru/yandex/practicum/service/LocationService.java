package ru.yandex.practicum.service;

import ru.yandex.practicum.dto.location.LocationDto;
import ru.yandex.practicum.dto.location.NewLocationDto;
import ru.yandex.practicum.dto.location.AdminLocationUpdateDto;

import java.util.List;

public interface LocationService {

    List<LocationDto> getLocations(Integer from, Integer size);

    LocationDto getById(Long locationId);

    LocationDto addLocation(NewLocationDto newLocationDto);

    LocationDto updateLocation(Long locationId, AdminLocationUpdateDto adminLocationUpdateDto);

    LocationDto addOrGetLocation(NewLocationDto newLocationDto);

    List<LocationDto> getByRadius(Float lat, Float lon, Float radius);

    void delete(Long locationId);
}
