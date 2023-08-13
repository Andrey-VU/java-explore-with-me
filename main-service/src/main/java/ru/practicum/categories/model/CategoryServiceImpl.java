package ru.practicum.categories.model;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.categories.dto.CategoryDto;
import ru.practicum.categories.dto.NewCategoryDto;
import ru.practicum.categories.mapper.CategoryMapperImpl;
import ru.practicum.categories.repo.CategoryRepo;

import java.util.List;

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
        //// В случае, если по заданным фильтрам не найдено ни одной категории, возвращает пустой список

        return null;
    }

    @Override
    public CategoryDto getPublic(Long catId) {
        return null;
    }

    private boolean isConnectedWithEvent(Long id) {
        return true;
    }
//    ТРЕБУЕТСЯ ДОРАБОТКА МЕТОДА
}
