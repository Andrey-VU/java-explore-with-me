package ru.practicum.category.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class NewCategoryDto {
    @Size(min = 1, max = 50)
    @NotBlank(message = "Необходимо ввести название категории")
    private String name;
}
