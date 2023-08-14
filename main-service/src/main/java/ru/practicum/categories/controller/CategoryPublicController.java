package ru.practicum.categories.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.categories.dto.CategoryDto;
import ru.practicum.categories.model.CategoryService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Validated
@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping(path = "/categories")
public class CategoryPublicController {
    private final CategoryService categoryService;

    @GetMapping
    List<CategoryDto> getCategories(@Positive @RequestParam(required = false, defaultValue = "10") int size,
                                    @PositiveOrZero @RequestParam (required = false, defaultValue = "0") int from){
        log.info("STARTED Getting public Categories with parameters: \n size {} \n from {}  ", size, from);
        return categoryService.getPublic(from, size);
    }

    @GetMapping("/{catId}")
    CategoryDto getCategory(@Positive @PathVariable Long catId){
        log.info("STARTED getting a Category with id {}", catId);
        return categoryService.getPublic(catId);
    }


}
