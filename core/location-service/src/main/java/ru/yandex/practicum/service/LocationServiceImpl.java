package ru.yandex.practicum.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.client.EventClient;
import ru.yandex.practicum.dto.event.EventFullDto;
import ru.yandex.practicum.dto.location.LocationDto;
import ru.yandex.practicum.dto.location.NewLocationDto;
import ru.yandex.practicum.dto.location.AdminLocationUpdateDto;
import ru.yandex.practicum.exception.ConflictException;
import ru.yandex.practicum.exception.DataRetrievalException;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.mapper.LocationMapper;
import ru.yandex.practicum.model.Location;
import ru.yandex.practicum.repository.LocationRepository;
import ru.yandex.practicum.util.PagingUtil;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional(readOnly = true)
public class LocationServiceImpl implements LocationService {

    LocationRepository locationRepository;
    LocationMapper locationMapper;
    EventClient eventClient;

    @Override
    public List<LocationDto> getLocations(Integer from, Integer size) {
        log.info("getLocations by from {} size {}", from, size);
        return locationRepository.findAll(PagingUtil.pageOf(from, size)).stream()
                .map(locationMapper::toDto).toList();
    }

    @Override
    public LocationDto getById(Long locationId) {
        log.info("getById params: id = {}", locationId);
        Location location = locationRepository.findById(locationId)
                .orElseThrow(
                        () -> new NotFoundException(String.format("Локация с ид %s не найдена", locationId))
                );
        log.info("getById result location = {}", location);
        return locationMapper.toDto(location);
    }

    @Override
    @Transactional
    public LocationDto addLocation(NewLocationDto newLocationDto) {
        Location location = locationRepository.save(locationMapper.toLocation(newLocationDto));
        log.info("Location is created: {}", location);
        return locationMapper.toDto(location);
    }

    @Override
    @Transactional
    public LocationDto updateLocation(Long locationId,
                                      AdminLocationUpdateDto adminLocationUpdateDto) {
        log.info("Updating location with ID: {}, new data: {}", locationId, adminLocationUpdateDto);

        // Находим существующую локацию или выбрасываем исключение
        Location existingLocation = locationRepository.findById(locationId)
                .orElseThrow(() -> new NotFoundException("Location not found with ID: " + locationId));

        // Обновляем данные локации на основе DTO
        Location updatedLocation = locationMapper.update(existingLocation, adminLocationUpdateDto);

        // Сохраняем обновленную локацию
        Location savedLocation = locationRepository.save(updatedLocation);

        log.info("Location updated successfully: {}", savedLocation);

        // Преобразуем в DTO и возвращаем
        return locationMapper.toDto(savedLocation);
    }

    @Override
    @Transactional
    public LocationDto addOrGetLocation(NewLocationDto newLocationDto) {
        Location location;
        if (newLocationDto == null) {
            location = null;
        } else {
            // Пытаемся найти существующую локацию в базе
            Optional<Location> existingLocation = locationRepository.findByLatAndLon(
                    newLocationDto.getLat(),
                    newLocationDto.getLon()
            );

            // Если локация не найдена, создаем и сохраняем новую
            location = existingLocation.orElseGet(() -> {
                Location newLocation = locationMapper.toLocation(newLocationDto);
                return locationRepository.save(newLocation);
            });
        }

        return locationMapper.toDto(location);
    }

    @Override
    public List<LocationDto> getByRadius(Double lat, Double lon, Double radius) {
        log.debug("Fetching locations within radius - lat: {}, lon: {}, radius: {}", lat, lon, radius);

        try {
            List<Location> locations = locationRepository.findAllByRadius(lat, lon, radius);

            if (locations.isEmpty()) {
                log.info("No locations found within the specified radius");
            } else {
                log.debug("Found {} locations within radius", locations.size());
            }

            List<LocationDto> result = locations.stream()
                    .map(locationMapper::toDto)
                    .toList();

            log.debug("Returning {} location DTOs", result.size());
            return result;
        } catch (Exception e) {
            log.error("Error fetching locations by radius", e);
            throw new DataRetrievalException("Failed to get locations by radius", e);
        }
    }

    @Override
    @Transactional
    public void delete(Long locationId) {
        List<EventFullDto> events = eventClient.getByLocation(locationId);
        if (!events.isEmpty()) {
            throw new ConflictException("location is used in events");
        }
        locationRepository.deleteById(locationId);
        log.info("Location deleted with id: {}", locationId);
    }
}