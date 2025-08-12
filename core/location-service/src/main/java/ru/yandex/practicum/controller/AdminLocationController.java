package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.location.AdminLocationUpdateDto;
import ru.yandex.practicum.dto.location.LocationDto;
import ru.yandex.practicum.dto.location.NewLocationDto;
import ru.yandex.practicum.service.LocationService;

@RestController
@RequestMapping(path = "/admin/locations")
@RequiredArgsConstructor
@Validated
@Slf4j
public class AdminLocationController {
    private final LocationService locationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LocationDto addLocation(@RequestBody @Valid NewLocationDto newLocationDto) {
        log.info("POST /admin/locations with body({})", newLocationDto);
        return locationService.addLocation(newLocationDto);
    }

    @PatchMapping("/{locationId}")
    public LocationDto updateLocation(@PathVariable(name = "locationId") Long locationId,
                                      @RequestBody @Valid AdminLocationUpdateDto adminLocationUpdateDto) {
        log.info("PATCH /admin/locations with body({})", adminLocationUpdateDto);
        return locationService.updateLocation(locationId, adminLocationUpdateDto);
    }

    @DeleteMapping("/{locationId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable(name = "locationId") Long locationId) {
        log.info("DELETE /admin/locations/{locationId} locationId = {})", locationId);
        locationService.delete(locationId);
    }
}
