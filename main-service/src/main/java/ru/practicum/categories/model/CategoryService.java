package ru.practicum.categories.model;

import ru.practicum.categories.dto.CategoryDto;
import ru.practicum.categories.dto.NewCategoryDto;

public interface CategoryService {
    CategoryDto add(NewCategoryDto newCategoryDto);
    CategoryDto update(Long id, NewCategoryDto newCategoryDto);
    void delete (Long id);
}
