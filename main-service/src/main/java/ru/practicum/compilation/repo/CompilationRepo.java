package ru.practicum.compilation.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.compilation.model.Compilation;

public interface CompilationRepo extends JpaRepository<Compilation, Long> {
}
