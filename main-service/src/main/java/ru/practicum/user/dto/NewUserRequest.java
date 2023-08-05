package ru.practicum.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@ToString
@Getter
@NoArgsConstructor
public class NewUserRequest {
    @NotBlank(message = "Регистрация без имени невозможна!")
    @Size(min = 2, max = 250)
    private String name;
    @Email(message = "Введите корректный электронный адрес!")
    @NotBlank(message = "Поле email не может быть пустым!")
    @Size(min = 6, max = 254)
    private String email;
}
