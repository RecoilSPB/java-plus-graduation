package ru.yandex.practicum.model;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Builder
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserAction {

    @NotNull
    Long userId;

    @NotNull
    Long eventId;

    @NotNull
    ActionType actionType;

    @Builder.Default
    Instant timestamp = Instant.now();
}