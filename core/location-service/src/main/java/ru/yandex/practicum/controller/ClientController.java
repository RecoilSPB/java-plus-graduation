package ru.yandex.practicum.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.client.LocationClient;
import ru.yandex.practicum.dto.location.LocationDto;
import ru.yandex.practicum.dto.location.NewLocationDto;
import ru.yandex.practicum.service.LocationService;

import java.util.List;

@RestController
@RequestMapping(path = "/internal/api/locations")
@RequiredArgsConstructor
public class ClientController implements LocationClient {
    private final LocationService locationService;

    @Override
    public LocationDto addOrGetLocation(NewLocationDto newLocationDto) {
        return locationService.addOrGetLocation(newLocationDto);
    }

    @Override
    public List<LocationDto> getByRadius(Float lat, Float lon, Float radius) {
        return locationService.getByRadius(lat, lon, radius);
    }

    @Override
    public LocationDto getById(Long locationId) {
        return locationService.getById(locationId);
    }
}
