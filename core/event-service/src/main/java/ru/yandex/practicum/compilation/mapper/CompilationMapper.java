package ru.yandex.practicum.compilation.mapper;

import lombok.experimental.UtilityClass;
import org.mapstruct.Mapper;
import ru.yandex.practicum.compilation.model.Compilation;
import ru.yandex.practicum.dto.compilation.NewCompilationDto;
import ru.yandex.practicum.dto.compilation.ResponseCompilationDto;

@Mapper(componentModel = "spring")
public interface CompilationMapper {
    Compilation mapToCompilation(NewCompilationDto dto);

    ResponseCompilationDto mapToResponseCompilation(Compilation compilation);
}
