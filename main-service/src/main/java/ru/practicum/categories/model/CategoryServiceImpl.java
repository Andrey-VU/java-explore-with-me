package ru.practicum.categories.model;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.categories.dto.CategoryDto;
import ru.practicum.categories.dto.NewCategoryDto;
import ru.practicum.categories.mapper.CategoryMapperImpl;
import ru.practicum.categories.repo.CategoryRepo;
import ru.practicum.exception.NotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
        Category categoryFromRepo = categoryRepo.getReferenceById(id);
        if (newCategoryDto.getName().equals(categoryFromRepo.getName())) {
            log.warn("Обновление для категории {} идентично сохранённому значению.\n" +
                "Обновление не выполнено!", categoryFromRepo.getName());
        } else {
            String oldName = categoryFromRepo.getName();
            categoryFromRepo.setName(newCategoryDto.getName());
            categoryFromRepo = categoryRepo.save(categoryFromRepo);
            log.info("Имя \"{}\" категории id {} изменено на \"{}\"", oldName, id, categoryFromRepo.getName() );
        }
        return categoryMapper.makeDto(categoryFromRepo);
    }

    @Override
    public void delete(Long id) {
        Category category = categoryRepo.getReferenceById(id);
        if (!isConnectedWithEvent(id)) {
            categoryRepo.delete(category);
            log.info("Удаление категории {} ЗАВЕРШЕНО", category);
        }
        else {
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
        return false;
    }
//    ТРЕБУЕТСЯ ДОРАБОТКА МЕТОДА
}
