package ru.yandex.practicum.compilation.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.yandex.practicum.compilation.model.Compilation;
import ru.yandex.practicum.dto.compilation.NewCompilationDto;
import ru.yandex.practicum.dto.compilation.ResponseCompilationDto;
import ru.yandex.practicum.dto.compilation.UpdateCompilationRequestDto;
import ru.yandex.practicum.dto.event.EventShortDto;
import ru.yandex.practicum.event.model.Event;

import java.util.List;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CompilationMapper {

    @Mapping(target = "events", source = "events")
    @Mapping(target = "id", ignore = true)
    Compilation toEntity(NewCompilationDto dto, List<Event> events);

    @Mapping(target = "events", source = "eventShortDtoList")
    ResponseCompilationDto toDto(Compilation compilation, List<EventShortDto> eventShortDtoList);

    @Mapping(target = "id", source = "compId")
    @Mapping(target = "events", source = "events")
    void update(UpdateCompilationRequestDto update, Long compId, List<Event> events,
                @MappingTarget Compilation destination);
}
