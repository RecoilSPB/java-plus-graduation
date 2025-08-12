package ru.yandex.practicum.compilation.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.compilation.mapper.CompilationMapper;
import ru.yandex.practicum.compilation.model.Compilation;
import ru.yandex.practicum.compilation.repository.CompilationRepository;
import ru.yandex.practicum.dto.compilation.NewCompilationDto;
import ru.yandex.practicum.dto.compilation.ResponseCompilationDto;
import ru.yandex.practicum.dto.compilation.UpdateCompilationRequestDto;
import ru.yandex.practicum.dto.event.EventShortDto;
import ru.yandex.practicum.event.mapper.EventMapper;
import ru.yandex.practicum.event.model.Event;
import ru.yandex.practicum.event.repository.EventRepository;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.util.PagingUtil;

import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CompilationServiceImpl implements CompilationService {
    final CompilationRepository compilationRepository;
    final EventRepository eventRepository;

    final CompilationMapper compilationMapper;
    final EventMapper eventMapper;

    @Override
    @Transactional
    public List<ResponseCompilationDto> getCompilations(Boolean pinned, Integer from, Integer size) {
        log.info("getCompilations params: pinned = {}, from = {}, size = {}", pinned, from, size);
        PageRequest page = PagingUtil.pageOf(from, size);

        return compilationRepository.findAllByPinned(pinned, page)
                .map(compilation -> {
                    List<EventShortDto> eventShortDtoList = eventMapper.toEventShortDtoList(compilation.getEvents());
                    return compilationMapper.toDto(compilation, eventShortDtoList);
                })
                .getContent();
    }

    @Override
    public ResponseCompilationDto getCompilationById(Long compilationId) {
        log.info("getById params: id = {}", compilationId);
        Compilation compilation = compilationRepository.findById(compilationId).orElseThrow(
                () -> new NotFoundException(String.format("Подборка с ид %s не найдена", compilationId))
        );
        log.info("getById result compilation = {}", compilation);

        List<EventShortDto> eventShortDtoList = eventMapper.toEventShortDtoList(compilation.getEvents());
        return compilationMapper.toDto(compilation, eventShortDtoList);
    }

    @Override
    @Transactional
    public ResponseCompilationDto addCompilation(NewCompilationDto compilationRequestDto) {
        log.info("addCompilation params: compilationRequestDto = {}", compilationRequestDto);

        List<Event> events = getAndCheckEventList(compilationRequestDto.getEvents());
        Compilation entity = compilationMapper.toEntity(compilationRequestDto, events);
        Compilation compilation = compilationRepository.save(entity);
        log.info("addCompilation result compilation = {}", compilation);
        return compilationMapper.toDto(compilation, eventMapper.toEventShortDtoList(compilation.getEvents()));
    }

    @Override
    @Transactional
    public ResponseCompilationDto updateCompilation(Long compilationId,
                                                    UpdateCompilationRequestDto compilationRequestDto) {
        log.info("update params: compilationId = {}, compilationRequestDto = {}", compilationId, compilationRequestDto);
        Compilation compilation = compilationRepository.findById(compilationId)
                .orElseThrow(() -> new NotFoundException("Указанная подборка не найдена " + compilationId));

        List<Event> events = getAndCheckEventList(compilationRequestDto.getEvents());
        compilationMapper.update(compilationRequestDto, compilationId, events, compilation);
        compilation = compilationRepository.save(compilation);
        log.info("updateCompilation result compilation = {}", compilation);
        List<EventShortDto> eventShortDtoList = eventMapper.toEventShortDtoList(compilation.getEvents());

        return compilationMapper.toDto(compilation, eventShortDtoList);
    }

    @Override
    @Transactional
    public void deleteCompilation(Long compilationId) {
        log.info("delete params: compilationId = {}", compilationId);
        compilationRepository.deleteById(compilationId);
    }

    private List<Event> getAndCheckEventList(List<Long> eventIds) {
        log.info("getAndCheckEventList params: eventIds = {}", eventIds);
        if (eventIds == null || eventIds.isEmpty()) {
            return Collections.emptyList();
        } else {
            List<Event> events = eventRepository.findAllById(eventIds);
            log.info("getAndCheckEventList result: events = {}", events);
            if (events.size() != eventIds.size()) {
                throw new NotFoundException("Некорректный список событий");
            }

            return events;
        }
    }

}
