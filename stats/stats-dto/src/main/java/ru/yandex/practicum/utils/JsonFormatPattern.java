package ru.yandex.practicum.utils;

import lombok.experimental.UtilityClass;
import java.time.format.DateTimeFormatter;

@UtilityClass
public class JsonFormatPattern {
    public static final String JSON_FORMAT_PATTERN_FOR_TIME = "yyyy-MM-dd HH:mm:ss";

    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(JSON_FORMAT_PATTERN_FOR_TIME);
}
