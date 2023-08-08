package ru.practicum.categories.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.categories.model.Category;

public interface CategoryRepo extends JpaRepository<Category, Long> {
}
