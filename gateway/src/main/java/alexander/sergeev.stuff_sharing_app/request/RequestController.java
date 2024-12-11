package alexander.sergeev.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Slf4j
@Controller
@RequestMapping("/requests")
@Validated
@RequiredArgsConstructor
public class RequestController {

    private final alexander.sergeev.request.RequestClient requestClient;

    @GetMapping
    public ResponseEntity<Object> getAllRequesterRequests(
            HttpServletRequest request,
            @RequestParam(value = "from", defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(value = "size", defaultValue = "20") @Min(1) @Max(20) Integer size,
            @RequestHeader(header) @Positive Long requesterId) {
        log.info("Id-{} {} {}?{}", requesterId, request.getMethod(), request.getRequestURI(), request.getQueryString());
        return requestClient.getAllRequesterRequests(requesterId, from, size);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequests(
            HttpServletRequest request,
            @RequestParam(value = "from", defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(value = "size", defaultValue = "20") @Min(1) @Max(20) Integer size,
            @RequestHeader(header) @Positive Long userId) {
        log.info("Id-{} {} {}?{}", userId, request.getMethod(), request.getRequestURI(), request.getQueryString());
        return requestClient.getAllRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestById(
            HttpServletRequest request,
            @RequestHeader(header) @Positive Long userId,
            @PathVariable @Positive Long requestId) {
        log.info("Id-{} {} {}", userId, request.getMethod(), request.getRequestURI());
        return requestClient.getRequestById(userId, requestId);
    }

    @PostMapping
    public ResponseEntity<Object> postRequest(
            HttpServletRequest request,
            @RequestHeader(header) @Positive Long requesterId,
            @RequestBody @Validated(ValidationMarker.OnCreate.class) IncomingRequestDto incomingRequestDto) {
        log.info("Id-{} {} {} {}", requesterId, request.getMethod(), request.getRequestURI(), incomingRequestDto);
        return requestClient.postRequest(requesterId, incomingRequestDto);
    }

}
