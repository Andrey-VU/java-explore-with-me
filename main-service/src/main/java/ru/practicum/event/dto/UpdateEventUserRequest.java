package ru.practicum.event.dto;

import ru.practicum.event.enums.StateActionUser;

public class UpdateEventUserRequest extends UpdateEventRequestAbstract {
    private StateActionUser stateAction;
    // Если поле в запросе не указано (равно null) - значит изменение этих данных не требуется.
}
