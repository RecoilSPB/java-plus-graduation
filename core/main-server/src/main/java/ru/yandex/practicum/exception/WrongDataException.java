package ru.yandex.practicum.exception;

public class WrongDataException extends Exception {
    public WrongDataException(String message) {
        super(message);
    }
}