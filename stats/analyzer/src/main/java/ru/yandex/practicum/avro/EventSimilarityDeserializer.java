package ru.yandex.practicum.avro;

import ru.yandex.practicum.deserializer.BaseAvroDeserializer;
import ru.yandex.practicum.stats.avro.EventSimilarityAvro;

public class EventSimilarityDeserializer extends BaseAvroDeserializer<EventSimilarityAvro> {
    public EventSimilarityDeserializer() {
        super(EventSimilarityAvro.getClassSchema());
    }
}
