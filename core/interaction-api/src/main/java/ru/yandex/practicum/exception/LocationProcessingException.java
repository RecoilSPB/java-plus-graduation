package ru.yandex.practicum.exception;

public class LocationProcessingException extends RuntimeException {
    public LocationProcessingException(String message) {
        super(message);
    }

    public LocationProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}
