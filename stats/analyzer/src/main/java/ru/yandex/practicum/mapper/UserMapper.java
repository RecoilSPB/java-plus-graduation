package ru.yandex.practicum.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.ewm.stats.avro.ActionTypeAvro;
import ru.yandex.practicum.ewm.stats.avro.UserActionAvro;
import ru.yandex.practicum.model.ActionType;
import ru.yandex.practicum.model.UserAction;

@Component
public class UserMapper {
    public static ActionType toActionType(ActionTypeAvro actionTypeAvro) {
        return ActionType.valueOf(actionTypeAvro.name());
    }

    public UserAction mapToUserAction(UserActionAvro userActionAvro) {
        return UserAction.builder()
                .userId(userActionAvro.getUserId())
                .eventId(userActionAvro.getEventId())
                .actionType(toActionType(userActionAvro.getActionType()))
                .created(userActionAvro.getTimestamp())
                .weight(toActionType(userActionAvro.getActionType()).getWeight())
                .build();
    }

}