package alexander.sergeev.stuff_sharing_app.user.request.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RequestServiceImplTest {

    @InjectMocks
    private RequestServiceImpl requestServiceImp;

    @Mock
    private RequestRepository requestRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    private final Pageable pageable = PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "created"));
    private final User owner = new User(
            1L,
            "Owner name",
            "owner@email.com");
    private final User requester = new User(
            2L,
            "Requester name",
            "requester@email.com");
    private final Request request = new Request(
            1L,
            "Request description",
            LocalDateTime.of(2000, 1, 1, 1, 1, 1),
            requester);
    private final Item item = new Item(
            1L,
            "Item name",
            "Item description",
            true,
            request,
            owner);
    private final IncomingRequestDto incomingRequestDto = new IncomingRequestDto(
            null,
            "Request description");

    @Test
    void getAllRequesterRequests_whenInvoke_shouldInvokeRepositoriesMethods() {
        when(requestRepository.findByRequesterId(2L, pageable))
                .thenReturn(List.of(request));
        when(itemRepository.findByRequestIdIn(List.of(1L)))
                .thenReturn(List.of(item));
        OutgoingItemDto outgoingItemDto = ItemMapper.mapItemToOutgoingDto(item);
        OutgoingRequestDto outgoingRequestDto = RequestMapper.mapRequestToOutgoingDto(request);
        outgoingRequestDto.setItems(List.of(outgoingItemDto));
        List<OutgoingRequestDto> expected = List.of(outgoingRequestDto);
        List<OutgoingRequestDto> result = requestServiceImp.getAllRequesterRequests(2L, pageable);
        assertEquals(expected, result);
        verify(userRepository).checkUserById(2L);
        verify(requestRepository).findByRequesterId(2L, pageable);
        verify(itemRepository).findByRequestIdIn(List.of(1L));
    }

    @Test
    void getAllRequests_whenInvoke_shouldInvokeRepositoryMethods() {
        when(requestRepository.findByRequesterIdIsNot(1L, pageable))
                .thenReturn(List.of(request));
        when(itemRepository.findByRequestIdIn(List.of(1L)))
                .thenReturn(List.of(item));
        OutgoingRequestDto outgoingRequestDto = RequestMapper.mapRequestToOutgoingDto(request);
        OutgoingItemDto outgoingItemDto = ItemMapper.mapItemToOutgoingDto(item);
        outgoingRequestDto.setItems(List.of(outgoingItemDto));
        List<OutgoingRequestDto> expected = List.of(outgoingRequestDto);
        List<OutgoingRequestDto> result = requestServiceImp.getAllRequests(1L, pageable);
        assertEquals(expected, result);
        verify(userRepository).checkUserById(1L);
        verify(requestRepository).findByRequesterIdIsNot(1L, pageable);
        verify(itemRepository).findByRequestIdIn(List.of(1L));
    }

    @Test
    void getRequestById_whenInvoke_shouldInvokeRepositoryMethods() {
        when(requestRepository.findRequestById(1L))
                .thenReturn(request);
        when(itemRepository.findByRequestId(1L))
                .thenReturn(List.of(item));
        OutgoingRequestDto expected = RequestMapper.mapRequestToOutgoingDto(request);
        OutgoingItemDto outgoingItemDto = ItemMapper.mapItemToOutgoingDto(item);
        expected.setItems(List.of(outgoingItemDto));
        OutgoingRequestDto result = requestServiceImp.getRequestById(1L, 1L);
        assertEquals(expected, result);
        verify(userRepository).checkUserById(1L);
        verify(requestRepository).findRequestById(1L);
        verify(itemRepository).findByRequestId(1L);
    }

    @Test
    void postRequest_whenInvoke_shouldInvokeRepositoryMethods() {
        when(userRepository.getUserById(2L))
                .thenReturn(requester);
        when(requestRepository.save(any(Request.class)))
                .thenReturn(request);
        OutgoingRequestDto expected = RequestMapper.mapRequestToOutgoingDto(request);
        OutgoingRequestDto result = requestServiceImp.postRequest(2L, incomingRequestDto);
        assertEquals(expected, result);
        verify(userRepository).getUserById(2L);
        verify(requestRepository).save(any(Request.class));
    }

}
