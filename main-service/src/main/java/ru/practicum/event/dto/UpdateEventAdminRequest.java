package ru.practicum.event.dto;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.event.enums.StateActionAdmin;

@Getter
@Setter
public class UpdateEventAdminRequest extends UpdateEventRequestAbstract {
    private StateActionAdmin stateAction;
}
