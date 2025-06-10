package ru.yandex.practicum.category.service;

import ru.yandex.practicum.category.dto.CategoryDto;
import ru.yandex.practicum.exception.ConflictException;
import ru.yandex.practicum.exception.NotFoundException;

import java.util.List;

public interface CategoryService {
    CategoryDto addCategory(CategoryDto categoryDto) throws ConflictException;

    CategoryDto updateCategory(Long catId, CategoryDto categoryDto) throws NotFoundException, ConflictException;

    CategoryDto getCategoryById(Long catId) throws NotFoundException;

    List<CategoryDto> getAllCategories(Integer from, Integer size);

    void deleteCategory(Long catId) throws ConflictException, NotFoundException;
}
