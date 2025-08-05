package ru.yandex.practicum.dto.user;

import lombok.*;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class UserShortDto {
    private Long id;
    private String name;
}