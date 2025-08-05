package ru.yandex.practicum.dto.comment;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.util.JsonFormatPattern;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Accessors(chain = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class CommentDto {
    Long id;
    Long userId;
    Long eventId;
    @JsonProperty("isInitiator")
    boolean isInitiator;
    @Size(min = 1, max = 5000)
    @NotBlank
    String content;
    @JsonFormat(pattern = JsonFormatPattern.JSON_FORMAT_PATTERN_FOR_TIME)
    LocalDateTime created;
}
