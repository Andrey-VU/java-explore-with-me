package ru.practicum.request.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.event.model.Event;
import ru.practicum.event.service.EventService;
import ru.practicum.exception.EwmConflictException;
import ru.practicum.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.user.model.User;
import ru.practicum.user.model.UserService;

import java.util.List;

@Service
@AllArgsConstructor
public class requestServiceImpl implements RequestService{
    EventService eventService;
    UserService userService;

    @Override
    public List<ParticipationRequestDto> getRequestsListPrivate(Long userId, Long eventId) {
        return null;
    }

    @Override
    public EventRequestStatusUpdateResult getResultRequestsListPrivate(Long userId, Long eventId) {
        return null;
    }

    @Override
    public ParticipationRequestDto cancelPrivate(Long userId, Long requestId) {
        return null;
    }

    @Override
    public ParticipationRequestDto createPrivate(Long userId, Long eventId) {

        requestValidation(userId, eventId);

        return null;
    }


    @Override
    public List<ParticipationRequestDto> getPrivate(Long requesterId) {
        return null;
    }

    // Получение информации о заявках текущего пользователя на участие в чужих событиях
    //В случае, если по заданным фильтрам не найдено ни одной заявки, возвращает пустой список


    private void requestValidation(Long requesterId, Long eventId) {
        Event event = eventService.getEventById(eventId);
        User user = userService.getUserById(requesterId);
        if (event.getInitiator().equals(user)) {
            throw new EwmConflictException("Инициатор не может подавать заявки на участие в созданных им событиях");
        }



        //        Обратите внимание:
//        нельзя добавить повторный запрос (Ожидается код ошибки 409)
//        нельзя участвовать в неопубликованном событии (Ожидается код ошибки 409)
//        если у события достигнут лимит запросов на участие -
//        необходимо вернуть ошибку (Ожидается код ошибки 409)
//        если для события отключена пре-модерация запросов на участие,
//        то запрос должен автоматически перейти в состояние подтвержденного



    }

}
