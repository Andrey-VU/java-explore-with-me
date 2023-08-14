package ru.practicum.event.dto;

import ru.practicum.event.enums.StateActionAdmin;

//Данные для изменения информации о событии.
//Если поле в запросе не указано (равно null) - значит изменение этих данных не треубется.

public class UpdateEventAdminRequest extends UpdateEventRequestAbstract {
    private StateActionAdmin stateAction;
}
