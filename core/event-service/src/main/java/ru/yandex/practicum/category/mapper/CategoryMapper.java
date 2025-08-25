package ru.yandex.practicum.category.mapper;

import org.mapstruct.*;
import ru.yandex.practicum.category.model.Category;
import ru.yandex.practicum.dto.category.CategoryDto;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    @Mapping(target = "id", ignore = true)
    Category mapCategoryDto(CategoryDto categoryDto);

    CategoryDto mapCategory(Category category);

    @Mapping(target = "id", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Category update(@MappingTarget Category category, CategoryDto updateCategoryDto);
}
