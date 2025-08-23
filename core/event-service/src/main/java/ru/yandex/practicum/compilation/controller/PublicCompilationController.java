package ru.yandex.practicum.compilation.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.compilation.service.CompilationService;
import ru.yandex.practicum.dto.compilation.ResponseCompilationDto;

import java.util.List;

@RestController
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/compilations")
@RequiredArgsConstructor
public class PublicCompilationController {

    CompilationService compilationService;

    @GetMapping
    public List<ResponseCompilationDto> getAll(@RequestParam(required = false) Boolean pinned,
                                               @RequestParam(defaultValue = "0") Integer from,
                                               @RequestParam(defaultValue = "10") Integer size) {
        return compilationService.getCompilations(pinned, from, size);
    }

    @GetMapping("/{compId}")
    public ResponseCompilationDto getCompilationById(@PathVariable Long compId) {
        return compilationService.getCompilationById(compId);
    }


}
