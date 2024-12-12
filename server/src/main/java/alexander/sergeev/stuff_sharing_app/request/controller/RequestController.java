package alexander.sergeev.stuff_sharing_app.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/requests")
@Validated
@RequiredArgsConstructor
public class RequestController {

    private final Sort sortByCreatingDesc = Sort.by(Sort.Direction.DESC, "created");
    private final RequestService requestService;

    @GetMapping
    public Collection<OutgoingRequestDto> getAllRequesterRequests(
            HttpServletRequest request,
            @RequestParam(value = "from") Integer firstElement,
            @RequestParam(value = "size") Integer size,
            @RequestHeader(header) Long requesterId) {
        log.info("Id-{} {} {}?{}", requesterId, request.getMethod(), request.getRequestURI(), request.getQueryString());
        return requestService.getAllRequesterRequests(requesterId,
                PageRequest.of(firstElement / size, size, sortByCreatingDesc));
    }

    @GetMapping("/all")
    public Collection<OutgoingRequestDto> getAllRequests(
            HttpServletRequest request,
            @RequestParam(value = "from") Integer firstElement,
            @RequestParam(value = "size") Integer size,
            @RequestHeader(header) Long userId) {
        log.info("Id-{} {} {}?{}", userId, request.getMethod(), request.getRequestURI(), request.getQueryString());
        return requestService.getAllRequests(userId, PageRequest.of(firstElement / size, size, sortByCreatingDesc));
    }

    @GetMapping("/{requestId}")
    public OutgoingRequestDto getRequestById(
            HttpServletRequest request,
            @RequestHeader(header) Long userId,
            @PathVariable Long requestId) {
        log.info("Id-{} {} {}", userId, request.getMethod(), request.getRequestURI());
        return requestService.getRequestById(userId, requestId);
    }

    @PostMapping
    public OutgoingRequestDto postRequest(
            HttpServletRequest request,
            @RequestHeader(header) Long requesterId,
            @RequestBody IncomingRequestDto incomingRequestDto) {
        log.info("Id-{} {} {} {}", requesterId, request.getMethod(), request.getRequestURI(), incomingRequestDto);
        return requestService.postRequest(requesterId, incomingRequestDto);
    }

}
