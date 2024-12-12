package alexander.sergeev.stuff_sharing_app.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Transactional
@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final RequestRepository requestRepository;

    @Override
    public List<OutgoingRequestDto> getAllRequesterRequests(Long requesterId, Pageable pageable) {
        userRepository.checkUserById(requesterId);
        List<Request> requestList = requestRepository.findByRequesterId(requesterId, pageable);
        return joinItemsToRequestList(requestList);
    }

    @Override
    public List<OutgoingRequestDto> getAllRequests(Long userId, Pageable pageable) {
        userRepository.checkUserById(userId);
        List<Request> requestList = requestRepository.findByRequesterIdIsNot(userId, pageable);
        return joinItemsToRequestList(requestList);
    }

    @Override
    public OutgoingRequestDto getRequestById(Long userId, Long requestId) {
        userRepository.checkUserById(userId);
        Request request = requestRepository.findRequestById(requestId);
        List<OutgoingItemDto> itemList = itemRepository.findByRequestId(requestId)
                .stream()
                .map(ItemMapper::mapItemToOutgoingDto)
                .collect(toList());
        OutgoingRequestDto outgoingRequestDto = RequestMapper.mapRequestToOutgoingDto(request);
        outgoingRequestDto.setItems(itemList);
        return outgoingRequestDto;
    }

    @Override
    public OutgoingRequestDto postRequest(Long requesterId, IncomingRequestDto incomingRequestDto) {
        Request request = RequestMapper.mapIncomingDtoToRequest(incomingRequestDto);
        request.setRequester(userRepository.getUserById(requesterId));
        return RequestMapper.mapRequestToOutgoingDto(requestRepository.save(request));
    }

    private List<OutgoingRequestDto> joinItemsToRequestList(List<Request> requestList) {
        Map<Long, List<OutgoingItemDto>> outgoingItemDtoMap = itemRepository.findByRequestIdIn(requestList
                        .stream()
                        .map(Request::getId)
                        .collect(toList()))
                .stream()
                .map(ItemMapper::mapItemToOutgoingDto)
                .collect(Collectors.groupingBy(OutgoingItemDto::getRequestId, toList()));
        return requestList
                .stream()
                .map(RequestMapper::mapRequestToOutgoingDto)
                .peek(outgoingRequestDto -> outgoingRequestDto.setItems(outgoingItemDtoMap
                        .getOrDefault(outgoingRequestDto.getId(), List.of())))
                .collect(toList());
    }

}
