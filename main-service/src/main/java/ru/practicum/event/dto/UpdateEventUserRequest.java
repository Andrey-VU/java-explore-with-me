package ru.practicum.event.dto;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.event.enums.StateActionUser;

@Getter
@Setter
public class UpdateEventUserRequest extends UpdateEventRequestAbstract {
    private StateActionUser stateAction;
}
