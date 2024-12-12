package alexander.sergeev.stuff_sharing_app.request.service;

import org.springframework.data.domain.Pageable;

import java.util.List;

public interface RequestService {

    List<OutgoingRequestDto> getAllRequesterRequests(Long requesterId, Pageable pageable);

    List<OutgoingRequestDto> getAllRequests(Long userId, Pageable pageable);

    OutgoingRequestDto getRequestById(Long userId, Long requestId);

    OutgoingRequestDto postRequest(Long requesterId, IncomingRequestDto incomingRequestDto);

}
