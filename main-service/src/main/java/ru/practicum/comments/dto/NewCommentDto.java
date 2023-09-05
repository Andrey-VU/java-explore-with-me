package ru.practicum.comments.dto;

import lombok.Data;
import ru.practicum.comments.model.Reaction;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class NewCommentDto {
    @NotBlank(message = "поле text не может быть пустым")
    @Size(min = 20, max = 252)
    private String text;
    @NotNull
    private Reaction reaction;
    @Size(max = 500)
    private String proposal;
}
