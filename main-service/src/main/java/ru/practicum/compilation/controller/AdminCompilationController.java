package ru.practicum.compilation.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.UpdateCompilationRequest;
import ru.practicum.compilation.service.CompilationService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

@Validated
@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping("/admin/compilations")
public class AdminCompilationController {
    private CompilationService compilationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    CompilationDto create(@Valid @RequestBody NewCompilationDto newCompilationDto) {
        log.info("ADMIN ACCESS: Получен запрос на создание коллекции {}", newCompilationDto.getTitle());
        CompilationDto compilationDto = compilationService.create(newCompilationDto);
        return compilationDto;
    }

    @DeleteMapping("/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void delete(@Positive @PathVariable Long compId) {
        log.info("Получен запрос на удалении коллекции событий Id {}", compId);
        compilationService.delete(compId);
    }

    @PatchMapping("/{compId}")
    CompilationDto update(@Positive @PathVariable Long compId,
                          @Valid @RequestBody UpdateCompilationRequest updateCompilationRequest){
        log.info("Получен запрос на обновление коллекции событий Id {}", compId);
        CompilationDto compilationDto = compilationService.update(compId, updateCompilationRequest);
        log.info("Коллекция событий Id {} обновлена", compId);
        return compilationDto;
    }
}
