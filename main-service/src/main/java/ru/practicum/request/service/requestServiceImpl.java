package ru.practicum.request.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.event.service.EventService;
import ru.practicum.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.request.dto.ParticipationRequestDto;
import java.util.List;

@Service
@AllArgsConstructor
public class requestServiceImpl implements RequestService{
    EventService eventService;

    @Override
    public List<ParticipationRequestDto> getRequestsListPrivate(Long userId, Long eventId) {
        return null;
    }

    @Override
    public EventRequestStatusUpdateResult getResultRequestsListPrivate(Long userId, Long eventId) {
        return null;
    }

    @Override
    public ParticipationRequestDto cancel(Long userId, Long requestId) {
        return null;
    }

    @Override
    public ParticipationRequestDto create(Long userId, ParticipationRequestDto requestDto) {

        requestValidation(userId, requestDto);



        return null;
    }


    @Override
    public List<ParticipationRequestDto> get(Long userId) {
        return null;
    }

    //В случае, если по заданным фильтрам не найдено ни одной заявки, возвращает пустой список


    private void requestValidation(Long userId, ParticipationRequestDto requestDto) {
        //        Обратите внимание:
//        нельзя добавить повторный запрос (Ожидается код ошибки 409)
//        инициатор события не может добавить запрос на участие в своём событии (Ожидается код ошибки 409)
//        нельзя участвовать в неопубликованном событии (Ожидается код ошибки 409)
//        если у события достигнут лимит запросов на участие -
//        необходимо вернуть ошибку (Ожидается код ошибки 409)
//        если для события отключена пре-модерация запросов на участие,
//        то запрос должен автоматически перейти в состояние подтвержденного



    }

}
