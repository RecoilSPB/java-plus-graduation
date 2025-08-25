package ru.yandex.practicum.category.service;

import ru.yandex.practicum.dto.category.CategoryDto;

import java.util.List;

public interface CategoryService {
    CategoryDto addCategory(CategoryDto categoryDto);

    CategoryDto updateCategory(Long catId, CategoryDto categoryDto);

    CategoryDto getCategoryById(Long catId);

    List<CategoryDto> getAllCategories(Integer from, Integer size);

    void deleteCategory(Long catId);
}
