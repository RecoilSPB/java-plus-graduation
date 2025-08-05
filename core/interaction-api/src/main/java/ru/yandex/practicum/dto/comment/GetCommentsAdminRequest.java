package ru.yandex.practicum.dto.comment;

import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class GetCommentsAdminRequest {
    Long eventId;
    Integer from;
    Integer size;
}
