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
import ru.practicum.request.enums.RequestStatusAction;
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
public class RequestServiceImpl implements RequestService{
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

        Event event = eventRepo.findById(eventId).orElseThrow(() -> new NotFoundException("Событие не найдено!"));
        userService.getUserById(userId);

        List<Long> requestsId = requestDto.getRequestIds();
        List<Request> requests = requestRepo.findAllById(requestsId);
        Long participants = getConfirmedRequests(event);

        //статус можно изменить только у заявок, находящихся в состоянии ожидания (Ожидается код ошибки 409)
        if (requests.stream().filter(request -> !request.getStatus().equals(RequestState.PENDING)).count() > 0) {
            filterForRequestWithIncorrectStatus(requests, requestsId, userId, eventId, requestDto);
        }

        if (requestDto.getStatus().equals(RequestStatusAction.REJECTED)) {
            requests.forEach(request -> request.setStatus(RequestState.REJECTED));
            requests.forEach(request -> requestRepo.save(request));
            return new EventRequestStatusUpdateResult(null, requests.stream()
                .map(request -> requestMapper.makeRequestDto(request)).collect(Collectors.toList()));
        }

        //если для события лимит заявок равен 0 или отключена пре-модерация заявок, то подтверждение заявок не требуется
        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            requests.forEach(request -> request.setStatus(RequestState.CONFIRMED));
            requests.forEach(request -> requestRepo.save(request));
            log.info("Подтверждение заявок не требуется - отключена премодерация, " +
                "либо отсутствует лимит.  {} заявок ПОДТВЕРЖДЕНЫ", requestsId.size());
            return new EventRequestStatusUpdateResult(requests.stream()
                .map(requestMapper::makeRequestDto).collect(Collectors.toList()), null);
        }

        //нельзя подтвердить заявку, если уже достигнут лимит по заявкам на данное событие (Ожидается код ошибки 409)
        if (event.getParticipantLimit() <= participants){
            //requests.forEach(request -> request.setStatus(RequestState.REJECTED));
            log.warn("Достигнут лимит по заявкам");
            throw new EwmConflictException("Достигнут лимит по заявкам");
        }


        //если при подтверждении данной заявки, лимит заявок для события исчерпан, то все неподтверждённые заявки необходимо отклонить
        List<ParticipationRequestDto> requestDtoConfirmList = new ArrayList<>();
        List<ParticipationRequestDto> requestDtoRejectList = new ArrayList<>();
        int countForConfirm = (int) (event.getParticipantLimit() - participants);
        for (int i = 0; i < requests.size(); i++) {
            if (i < countForConfirm) {
                requests.get(i).setStatus(RequestState.CONFIRMED);
                Request confirmedRequest = requestRepo.save(requests.get(i));
                requestDtoConfirmList.add(requestMapper.makeRequestDto(confirmedRequest));
            }
            else {
                 requests.get(i).setStatus(RequestState.REJECTED);
                 Request confirmedRequest = requestRepo.save(requests.get(i));
                 requestDtoConfirmList.add(requestMapper.makeRequestDto(confirmedRequest));
                }
            }
        return new EventRequestStatusUpdateResult(requestDtoConfirmList, requestDtoRejectList);
    }

    private void filterForRequestWithIncorrectStatus(List<Request> requests, List<Long> requestsId, Long userId, Long eventId, EventRequestStatusUpdateRequest requestDto) {

        List<Request> incorrectStatus = requests.stream().filter(request -> !request.getStatus().equals(RequestState.PENDING)).collect(
            Collectors.toList());
        List<Long> incorrectStatusIds = incorrectStatus.stream().map(request -> request.getId()).collect(Collectors.toList());
        requestsId = requestsId.stream()
            .filter(id -> !incorrectStatusIds.contains(id))
            .collect(Collectors.toList());
        requestDto.setRequestIds(requestsId);

        updateResultRequestsListPrivate(userId, eventId, requestDto);

        if (incorrectStatus.size() > 0) {
            log.warn("У {} заявок установлен некорректный статус. Изменение НЕВОЗМОЖНО", incorrectStatus.size());
            throw new EwmConflictException("Статус заявок не позволяет внести обновления!");
        }
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

        if (event.getParticipantLimit() != 0 &&
            getConfirmedRequests(event) >= event.getParticipantLimit()) {
            request.setStatus(RequestState.REJECTED);
            log.warn("Заявке присвоен статус REJECTED, поскольку превышен лимит участников мероприятия");
            throw new EwmConflictException("Лимит участников исчерпан");
        }

        request = requestRepo.save(request);

        ParticipationRequestDto requestDto = requestMapper.makeRequestDto(request);
        log.info("СОЗДАН запрос на участие в событии Id: {}", requestDto.getEvent());

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
    public Long getConfirmedRequests(Event event) {
        List<Request> requests = requestRepo.findAllByEventId(event.getId());
        Long confirmedRequests = requests.stream()
                                     .filter(request -> request.getStatus().equals(RequestState.CONFIRMED))
                                     .count();
        log.info("Найдено {} подтверждённых участников у событие Id {}", confirmedRequests, event.getId());
        return confirmedRequests;
    }

    private void requestValidation(User requester, Event event) {
        List<Request> requests = requestRepo.findAll();

        Long participants = getConfirmedRequests(event);
        if (event.getParticipantLimit() <= participants) {
            throw new EwmConflictException("Достигнут лимит заявок");
        }

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
    }
}
