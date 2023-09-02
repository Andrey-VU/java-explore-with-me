package ru.practicum.category.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.NewCategoryDto;
import ru.practicum.category.model.CategoryService;

import javax.validation.Valid;

@Validated
@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping(path = "/admin/categories")
public class CategoryAdminController {
    private final CategoryService categoryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    CategoryDto add(@Valid @RequestBody NewCategoryDto newCategoryDto) {
        log.info("addCategory {}: STARTED", newCategoryDto.getName());
        return categoryService.add(newCategoryDto);
    }

    @DeleteMapping("/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void delete(@PathVariable Long catId) {
        log.info("Delete category id {}: STARTED", catId);
        categoryService.delete(catId);
    }

    @PatchMapping("/{catId}")
    CategoryDto update(@PathVariable Long catId,
                       @RequestBody @Valid NewCategoryDto newCategoryDto) {
        log.info("Update category id {}: STARTED", catId);
        return categoryService.update(catId, newCategoryDto);
    }
}
