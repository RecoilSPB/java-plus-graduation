package ru.yandex.practicum.exception;

public class DataRetrievalException extends RuntimeException {
    public DataRetrievalException(String message) {
        super(message);
    }

    public DataRetrievalException(String message, Throwable cause) {
        super(message, cause);
    }

}
