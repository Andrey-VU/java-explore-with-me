package ru.practicum.category.model;

import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.NewCategoryDto;

import java.util.List;

public interface CategoryService {
    CategoryDto add(NewCategoryDto newCategoryDto);
    CategoryDto update(Long id, NewCategoryDto newCategoryDto);
    void delete (Long id);

    List<CategoryDto> getPublic(int from, int size);

    CategoryDto getPublic(Long catId);
}
