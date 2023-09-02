package ru.practicum.category.model;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.NewCategoryDto;
import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.category.repo.CategoryRepo;
import ru.practicum.event.repo.EventRepo;
import ru.practicum.exception.EwmConflictException;
import ru.practicum.exception.NotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepo categoryRepo;
    private final EventRepo eventRepo;
    private CategoryMapper categoryMapper;

    @Override
    @Transactional
    public CategoryDto add(NewCategoryDto newCategoryDto) {

        isNewNameFree(newCategoryDto.getName());

        Category category = categoryRepo.save(categoryMapper.makeCategory(newCategoryDto));
        CategoryDto dto = categoryMapper.makeDto(category);
        log.info("New category {} has been added", dto.getName());
        return dto;
    }

    @Override
    @Transactional
    public CategoryDto update(Long id, NewCategoryDto newCategoryDto) {
        Category categoryFromRepo = categoryRepo.findById(id).orElseThrow(
            () -> new NotFoundException("Категория не обнаружена"));

        String oldName = categoryFromRepo.getName();
        if (!newCategoryDto.getName().equals(oldName)) {
            isNewNameFree(newCategoryDto.getName());
            categoryFromRepo.setName(newCategoryDto.getName());
            categoryFromRepo = categoryRepo.save(categoryFromRepo);
            log.info("Имя \"{}\" категории id {} изменено на \"{}\"", oldName, id, categoryFromRepo.getName());
        }

        return categoryMapper.makeDto(categoryFromRepo);
    }

    private void isNewNameFree(String name) {
        List<String> categoryNamesFromRepo = new ArrayList<>();

        if (!categoryRepo.findAll().isEmpty()) {
            for (Category category : categoryRepo.findAll()) {
                categoryNamesFromRepo.add(category.getName());
            }
            if (categoryNamesFromRepo.contains(name)) {
                log.warn("Нельзя присваивать категории не уникальное имя");
                throw new EwmConflictException("Попытка присвоить категории уже существующее название");
            }
        }
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Category category = categoryRepo.getReferenceById(id);
        if (!isConnectedWithEvent(id)) {
            categoryRepo.delete(category);
            log.info("Удаление категории {} ЗАВЕРШЕНО", category);
        } else {
            log.info("Категория связана с событиями и не может быть удалена");
        }
    }

    @Override
    public List<CategoryDto> getPublic(int from, int size) {
        List<CategoryDto> categoryDtos = new ArrayList<>();
        PageRequest pageRequest = PageRequest.of(from > 0 ? from / size : 0, size);
        categoryDtos = categoryRepo.findAll(pageRequest).stream()
            .map(category -> categoryMapper.makeDto(category))
            .collect(Collectors.toList());
        log.info("{} categories are found", categoryDtos.size());
        return categoryDtos;
    }

    @Override
    public CategoryDto getPublic(Long catId) {
        Category category = categoryRepo.findById(catId)
            .orElseThrow(() -> new NotFoundException("КАТЕГОРИЯ id "
                + catId + " не найдена"));

        log.info("Category {} is found", category.getName());
        return categoryMapper.makeDto(category);
    }

    private boolean isConnectedWithEvent(Long id) {
        if (eventRepo.findAll().stream()
            .filter(event -> event.getCategory().getId().equals(id)).count() > 0) {
            log.warn("Попытка удалить категорию, которая задействована в опубликованных событиях");
            throw new EwmConflictException("Категория используется. Удаление невозможно");
        }
        return false;
    }
}
