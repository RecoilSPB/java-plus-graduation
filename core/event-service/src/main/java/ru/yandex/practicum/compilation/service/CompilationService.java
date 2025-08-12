package ru.yandex.practicum.compilation.service;

import ru.yandex.practicum.dto.compilation.NewCompilationDto;
import ru.yandex.practicum.dto.compilation.ResponseCompilationDto;
import ru.yandex.practicum.dto.compilation.UpdateCompilationRequest;

import java.util.List;

public interface CompilationService {
    ResponseCompilationDto addCompilation(NewCompilationDto dto);

    ResponseCompilationDto updateCompilation(Long compId, UpdateCompilationRequest compilation);

    ResponseCompilationDto getCompilationById(Long id);

    List<ResponseCompilationDto> getCompilations(Boolean pinned, Integer from, Integer size);

    void deleteCompilation(Long id);
}
