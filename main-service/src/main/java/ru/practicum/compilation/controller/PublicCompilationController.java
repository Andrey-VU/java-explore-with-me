package ru.practicum.compilation.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.UpdateCompilationRequest;
import ru.practicum.compilation.service.CompilationService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping("/compilations")
public class PublicCompilationController {
    private CompilationService compilationService;

    @GetMapping
    List<CompilationDto> getAll(@RequestParam(required = false) Boolean pinned,
                                @RequestParam(required = false, defaultValue = "0") @PositiveOrZero Integer from,
                                @RequestParam(required = false, defaultValue = "10") @Positive Integer size) {
        log.info("PUBLIC ACCESS: Получен запрос на на подбор коллекции событий " +
            "со следующими параметрами pinned = {}, from = {}, size = {}", pinned, from, size);

        return null;
    }

    @GetMapping("/{compId}")
    void delete(@Positive @PathVariable Long compId) {
        log.info("PUBLIC ACCESS: Получен запрос на просмотр коллекции событий Id {}", compId);

    }
}
