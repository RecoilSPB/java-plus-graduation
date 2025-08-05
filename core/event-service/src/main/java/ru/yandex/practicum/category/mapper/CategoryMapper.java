package ru.yandex.practicum.category.mapper;

import lombok.experimental.UtilityClass;
import org.mapstruct.Mapper;
import ru.yandex.practicum.category.model.Category;
import ru.yandex.practicum.dto.category.CategoryDto;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    Category mapCategoryDto(CategoryDto categoryDto);

    CategoryDto mapCategory(Category category);

}
