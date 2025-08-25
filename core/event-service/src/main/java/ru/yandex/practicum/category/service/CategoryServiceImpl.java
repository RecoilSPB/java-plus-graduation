package ru.yandex.practicum.category.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.dto.category.CategoryDto;
import ru.yandex.practicum.category.mapper.CategoryMapper;
import ru.yandex.practicum.category.model.Category;
import ru.yandex.practicum.category.repository.CategoryRepository;
import ru.yandex.practicum.event.repository.EventRepository;
import ru.yandex.practicum.exception.ConflictException;
import ru.yandex.practicum.exception.NotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CategoryServiceImpl implements CategoryService {

    CategoryRepository categoryRepository;
    EventRepository eventRepository;
    CategoryMapper categoryMapper;

    @Override
    @Transactional
    public CategoryDto addCategory(CategoryDto categoryDto) {
        if (categoryRepository.existsByName(categoryDto.getName())) {
            throw new ConflictException("Такая категория событий уже существует");
        }
        var category = categoryRepository.save(categoryMapper.mapCategoryDto(categoryDto));
        return categoryMapper.mapCategory(category);
    }

    @Override
    @Transactional
    public CategoryDto updateCategory(Long catId, CategoryDto categoryDto) {
        // Найти категорию по ID, либо выбросить исключение, если не найдена
        log.info("start updateCategory");
        Category category = categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException("Категория с ID " + catId + " не найдена."));

        // Проверить, существует ли другая категория с таким же именем
        boolean categoryExists = categoryRepository.findByName(categoryDto.getName()).stream()
                .anyMatch(c -> !c.getId().equals(catId));

        if (categoryExists) {
            throw new ConflictException(String.format(
                    "Нельзя задать имя категории %s, поскольку такое имя уже используется.",
                    categoryDto.getName()
            ));
        }

        Category update = categoryMapper.update(category, categoryDto);
        category = categoryRepository.save(update);
        log.info("Category is updated: {}", category);

        return categoryMapper.mapCategory(category);
    }


    @Override
    public CategoryDto getCategoryById(Long catId) {
        Category category = categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException("Указанная категория не найдена " + catId));

        return categoryMapper.mapCategory(category);
    }

    @Override
    public List<CategoryDto> getAllCategories(Integer from, Integer size) {
        return categoryRepository.findAll(PageRequest.of(from / size, size))
                .getContent()
                .stream()
                .map(categoryMapper::mapCategory)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteCategory(Long catId) {
        if (!categoryRepository.existsById(catId)) {
            throw new NotFoundException(String.format("Категория с id=%d не существует", catId));
        }

        if (!eventRepository.existsByCategoryId(catId)) {
            categoryRepository.deleteById(catId);
        } else {
            throw new ConflictException("Невозможно удаление используемой категории события ");
        }
    }
}
