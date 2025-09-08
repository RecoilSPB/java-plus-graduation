package ru.yandex.practicum.service;

import ru.yandex.practicum.model.UserAction;

public interface ActionService {

    void collectUserAction(UserAction userAction);
}