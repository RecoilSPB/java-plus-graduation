package ru.yandex.practicum.compilation.service;

import ru.yandex.practicum.compilation.dto.NewCompilationDto;
import ru.yandex.practicum.compilation.dto.ResponseCompilationDto;
import ru.yandex.practicum.compilation.dto.UpdateCompilationRequest;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.exception.ValidationException;

import java.util.List;

public interface CompilationService {
    ResponseCompilationDto addCompilation(NewCompilationDto dto);

    ResponseCompilationDto updateCompilation(Long compId, UpdateCompilationRequest compilation) throws NotFoundException;

    ResponseCompilationDto getCompilationById(Long id) throws NotFoundException;

    List<ResponseCompilationDto> getCompilations(Boolean pinned, Integer from, Integer size);

    void deleteCompilation(Long id) throws ValidationException, NotFoundException;
}
