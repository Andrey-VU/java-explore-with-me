package ru.practicum.request.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.event.enums.EventState;
import ru.practicum.event.model.Event;
import ru.practicum.event.repo.EventRepo;
import ru.practicum.exception.EwmConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.enums.RequestState;
import ru.practicum.request.mapper.RequestMapper;
import ru.practicum.request.model.Request;
import ru.practicum.request.repo.RequestRepo;
import ru.practicum.user.model.User;
import ru.practicum.user.model.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class requestServiceImpl implements RequestService{
    private final EventRepo eventRepo;
    private final UserService userService;
    private final RequestRepo requestRepo;
    private final RequestMapper requestMapper;

    @Override
    public List<ParticipationRequestDto> getRequestsListPrivate(Long userId, Long eventId) {
        Event event = eventRepo.findById(eventId).orElseThrow(() -> new NotFoundException("Событие не найдено!"));
        userService.getUserById(userId);

        if (event.getInitiator().getId() != userId){
            log.warn("Пользователь id {} не является инициатором события id {}", userId, eventId);
            throw new NotFoundException("НЕ НАЙДЕНО событие id " + eventId + "у пользователя id " + userId);
        }

        List<ParticipationRequestDto> requestsDtoList = requestRepo.findAllByEventId(eventId).stream()
            .map(request -> requestMapper.makeRequestDto(request))
            .collect(Collectors.toList());
        log.info("НАЙДЕНО {} запросов для участия в событии id {}", requestsDtoList.size(), eventId);

        return requestsDtoList;
    }

    @Override
    public EventRequestStatusUpdateResult updateResultRequestsListPrivate(Long userId, Long eventId,
                                                                          EventRequestStatusUpdateRequest requestDto) {
        //Изменение статуса (подтверждена, отменена) заявок на участие в событии текущего пользователя
        //если для события лимит заявок равен 0 или отключена пре-модерация заявок, то подтверждение заявок не требуется
        //нельзя подтвердить заявку, если уже достигнут лимит по заявкам на данное событие (Ожидается код ошибки 409)
        //статус можно изменить только у заявок, находящихся в состоянии ожидания (Ожидается код ошибки 409)
        //если при подтверждении данной заявки, лимит заявок для события исчерпан,
        //то все неподтверждённые заявки необходимо отклонить
        Event event = eventRepo.findById(eventId).orElseThrow(() -> new NotFoundException("Событие не найдено!"));
        userService.getUserById(userId);
        Integer participants = Math.toIntExact(getConfirmedRequestsById(eventId));
        List<Long> requestsId = requestDto.getRequestIds();
        List<Request> requests = requestRepo.findAllByEventId(eventId);

        switch (requestDto.getStatus()){
            case REJECTED:
                requests.forEach(request -> request.setStatus(RequestState.REJECTED));
                return new EventRequestStatusUpdateResult(null, requests.stream()
                    .map(request -> requestMapper.makeRequestDto(request)).collect(Collectors.toList()));

            case CONFIRMED:
                if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
                    log.info("Подтверждение заявок не требуется - отключена премодерация, " +
                        "либо отсутствует лимит.  {} заявок ПОДТВЕРЖДЕНЫ", requestsId.size());
                    requests.forEach(request -> request.setStatus(RequestState.CONFIRMED));
                    return new EventRequestStatusUpdateResult(requests.stream()
                        .map(requestMapper::makeRequestDto).collect(Collectors.toList()), null);
                } else if (event.getParticipantLimit() <= participants) {
                    requests.forEach(request -> request.setStatus(RequestState.REJECTED));
                    return new EventRequestStatusUpdateResult(null, requests.stream()
                        .map(request -> requestMapper.makeRequestDto(request)).collect(Collectors.toList()));
                } else if (event.getParticipantLimit() - participants >= requests.size() ) {
                    requests.forEach(request -> request.setStatus(RequestState.CONFIRMED));
                    return new EventRequestStatusUpdateResult(requests.stream()
                        .map(requestMapper::makeRequestDto).collect(Collectors.toList()), null);
                } else if (event.getParticipantLimit() - participants < requests.size()) {

                    int newParticipants = event.getParticipantLimit() - participants;
                    List<Long> requestIdForConfirmList = new ArrayList<>();
                    List<Long> requestIdForRejectList = new ArrayList<>();

                    while (newParticipants > 0) {
                        requestIdForConfirmList.add(requestsId.get(0));
                        requestsId.remove(0);
                        newParticipants -= 1;
                    }

                    requestIdForRejectList = requestsId;

                    List<ParticipationRequestDto> requestDtoForRejectList
                        = requestRepo.findAllById(requestIdForRejectList).stream()
                        .map(request -> requestMapper.makeRequestDto(request))
                        .collect(Collectors.toList());

                    List<ParticipationRequestDto> requestDtoForConfirmList
                        = requestRepo.findAllById(requestIdForConfirmList).stream()
                        .map(request -> requestMapper.makeRequestDto(request))
                        .collect(Collectors.toList());

                    requests.forEach(request -> request.setStatus(RequestState.CONFIRMED));
                    return new EventRequestStatusUpdateResult(requestDtoForConfirmList, requestDtoForRejectList);
                }
        }
        return new EventRequestStatusUpdateResult();
    }

    @Override
    public ParticipationRequestDto cancelPrivate(Long requesterId, Long requestId) {
        Request request = requestRepo.findById(requestId)
            .orElseThrow(() -> new NotFoundException("Запрос id " + requestId + "НЕ НАЙДЕН!"));
        if (request.getRequester().getId() != requesterId) {
            throw new NotFoundException("У пользователя id " + requesterId + " НЕ НАЙДЕН запрос id " + requestId);
        }
        request.setStatus(RequestState.CANCELED);
        ParticipationRequestDto requestDto = requestMapper.makeRequestDto(requestRepo.save(request));
        return requestDto;
    }

    @Override
    public ParticipationRequestDto createPrivate(Long requesterId, Long eventId) {
        Event event = eventRepo.findById(eventId).orElseThrow(()
            -> new NotFoundException("Событие Id " + eventId + " не найдено!"));
        User user = userService.getUserById(requesterId);
        requestValidation(user, event);

        Request request = Request.builder()
            .created(LocalDateTime.now())
            .requester(user)
            .event(event)
            .status(RequestState.PENDING)
            .build();

        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            request.setStatus(RequestState.CONFIRMED);
        }

        request = requestRepo.save(request);

        ParticipationRequestDto requestDto = requestMapper.makeRequestDto(request);
        log.info("СОЗДАН запрос на участие в событии: {}", requestDto);

        return requestDto;
    }

    @Override
    public List<ParticipationRequestDto> getPrivate(Long requesterId) {
        List<ParticipationRequestDto> participationRequestDtoList = new ArrayList<>();
        participationRequestDtoList = requestRepo.findAllByRequesterId(requesterId).stream()
            .map(request -> requestMapper.makeRequestDto(request))
            .collect(Collectors.toList());
        log.info("Найдено {} заявок", participationRequestDtoList.size());
        return participationRequestDtoList;
    }

    @Override
    public Long getConfirmedRequestsById(Long eventId) {
        Event event = eventRepo.findById(eventId).orElseThrow(() -> new NotFoundException("Событие не найдено!"));
        List<Request> requests = requestRepo.findAll();
        Long confirmedRequests = requests.stream()
                                     .filter(request -> request.getEvent().equals(event))
                                     .count();
        log.info("Найдено {} подтверждённых участников у событие Id {}", confirmedRequests, eventId);
        return confirmedRequests;
    }

    private void requestValidation(User requester, Event event) {
        List<Request> requests = requestRepo.findAll();

        if (requests.stream()
            .filter(request -> request.getRequester().equals(requester))
            .filter(request -> request.getEvent().equals(event)).count() == 1) {
            throw new EwmConflictException("Невозможно отправить запрос на участие в событии ПОВТОРНО");
        }
        if (event.getInitiator().equals(requester)) {
            throw new EwmConflictException("Инициатор не может подавать заявки на участие в созданных им событиях");
        }
        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new EwmConflictException("Нельзя участвовать в неопубликованном событии");
        }
        if (event.getParticipantLimit() != 0 &&
            getConfirmedRequestsById(event.getId()) >= event.getParticipantLimit()) {
            throw new EwmConflictException("Достигнут лимит участников в данном мероприятии");
        }
    }
}
