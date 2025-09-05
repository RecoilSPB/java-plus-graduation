package ru.yandex.practicum.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.proto.ActionTypeProto;
import ru.practicum.ewm.stats.proto.UserActionProto;
import ru.yandex.practicum.model.ActionType;
import ru.yandex.practicum.model.UserAction;
import ru.yandex.practicum.stats.avro.ActionTypeAvro;
import ru.yandex.practicum.stats.avro.UserActionAvro;

import java.time.Instant;

@Component
public class UserActionMapper {
    public UserActionAvro toUserActionAvro(UserAction userAction) {
        return UserActionAvro.newBuilder()
                .setUserId(userAction.getUserId())
                .setEventId(userAction.getEventId())
                .setTimestamp(userAction.getTimestamp())
                .setActionType(toActionTypeAvro(userAction.getActionType()))
                .build();

    }

    public ActionTypeAvro toActionTypeAvro(ActionType actionType) {
        return ActionTypeAvro.valueOf(actionType.name());
    }


    public static UserAction toEntity(UserActionProto userActionProto) {
        return UserAction.builder()
                .userId(userActionProto.getUserId())
                .eventId(userActionProto.getEventId())
                .actionType(toActionType(userActionProto.getActionType()))
                .timestamp(Instant.ofEpochSecond(userActionProto.getTimestamp().getSeconds(),
                        userActionProto.getTimestamp().getNanos()))
                .build();
    }

    public static ActionType toActionType(ActionTypeProto actionTypeProto) {
        return switch (actionTypeProto) {
            case ACTION_VIEW -> ActionType.VIEW;
            case ACTION_REGISTER -> ActionType.REGISTER;
            case ACTION_LIKE -> ActionType.LIKE;
            default -> null;
        };
    }

}