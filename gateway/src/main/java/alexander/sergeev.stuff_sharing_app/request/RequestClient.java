package alexander.sergeev.stuff_sharing_app.request;

import alexander.sergeev.stuff_sharing_app.client.BaseClient;
import alexander.sergeev.stuff_sharing_app.request.dto.IncomingRequestDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.util.Map;

@Service
public class RequestClient extends BaseClient {

    private static final String API_PREFIX = "/requests";

    @Autowired
    public RequestClient(@Value("${stuff_sharing_app_server_url}") String serverUrl, RestTemplateBuilder builder) {
        super(builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build());
    }

    public ResponseEntity<Object> getAllRequesterRequests(Long requesterId, Integer from, Integer size) {
        return get("?from={from}&size={size}", requesterId, Map.of("from", from, "size", size));
    }

    public ResponseEntity<Object> getAllRequests(Long userId, Integer from, Integer size) {
        return get("/all?from={from}&size={size}", userId, Map.of("from", from, "size", size));
    }

    public ResponseEntity<Object> getRequestById(Long userId, Long requestId) {
        return get("/" + requestId, userId);
    }

    public ResponseEntity<Object> postRequest(Long requesterId, IncomingRequestDto incomingRequestDto) {
        return post("", requesterId, incomingRequestDto);
    }
}