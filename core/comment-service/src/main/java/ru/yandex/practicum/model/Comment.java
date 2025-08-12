package ru.yandex.practicum.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.util.JsonFormatPattern;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "comment")
@EqualsAndHashCode(of = "id")
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    Long userId;

    Long eventId;

    @Column(nullable = false, length = 5000)
    String content;

    @Column(name = "is_initiator", nullable = false)
    boolean isInitiator;

    @Column(name = "created", nullable = false)
    @JsonFormat(pattern = JsonFormatPattern.JSON_FORMAT_PATTERN_FOR_TIME)
    LocalDateTime created;
}
