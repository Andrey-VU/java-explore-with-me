package ru.practicum.category.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.category.model.Category;

public interface CategoryRepo extends JpaRepository<Category, Long> {
}
