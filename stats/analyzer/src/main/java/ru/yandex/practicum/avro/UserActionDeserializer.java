package ru.yandex.practicum.avro;

import ru.yandex.practicum.deserializer.BaseAvroDeserializer;
import ru.yandex.practicum.stats.avro.UserActionAvro;

public class UserActionDeserializer extends BaseAvroDeserializer<UserActionAvro> {
    public UserActionDeserializer() {
        super(UserActionAvro.getClassSchema());
    }
}