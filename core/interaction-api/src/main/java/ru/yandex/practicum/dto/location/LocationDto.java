package ru.yandex.practicum.dto.location;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class LocationDto {
    private Long id;

    private Float lat;

    private Float lon;

    private String name;

    private String address;
}