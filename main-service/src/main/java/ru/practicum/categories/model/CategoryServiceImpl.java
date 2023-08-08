package ru.practicum.categories.model;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.categories.dto.CategoryDto;
import ru.practicum.categories.dto.NewCategoryDto;
import ru.practicum.categories.mapper.CategoryMapperImpl;
import ru.practicum.categories.repo.CategoryRepo;

@Service
@Slf4j
@AllArgsConstructor
public class CategoryServiceImpl implements CategoryService{
    private final CategoryRepo categoryRepo;
    private CategoryMapperImpl categoryMapper;

    @Override
    public CategoryDto add(NewCategoryDto newCategoryDto) {
        Category category = categoryRepo.save(categoryMapper.makeCategory(newCategoryDto));
        CategoryDto dto = categoryMapper.makeDto(category);
        log.info("New category {} has been added", dto.getName());
        return dto;
    }

    @Override
    public CategoryDto update(Long id, NewCategoryDto newCategoryDto) {
        return null;
    }

    @Override
    public void delete(Long id) {

    }
}
