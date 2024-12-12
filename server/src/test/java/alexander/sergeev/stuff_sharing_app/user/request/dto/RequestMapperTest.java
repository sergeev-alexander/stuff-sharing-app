package alexander.sergeev.stuff_sharing_app.user.request.dto;

import alexander.sergeev.stuff_sharing_app.request.dto.IncomingRequestDto;
import alexander.sergeev.stuff_sharing_app.request.dto.OutgoingRequestDto;
import alexander.sergeev.stuff_sharing_app.request.dto.RequestMapper;
import alexander.sergeev.stuff_sharing_app.request.model.Request;
import alexander.sergeev.stuff_sharing_app.user.model.User;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RequestMapperTest {

    @Test
    void mapIncomingDtoToRequest() {
        IncomingRequestDto incomingRequestDto = new IncomingRequestDto(
                null,
                "Some incomingRequestDto description");
        Request expected = new Request(
                null,
                "Some incomingRequestDto description",
                null,
                null);
        Request result = RequestMapper.mapIncomingDtoToRequest(incomingRequestDto);
        assertEquals(expected.getDescription(), result.getDescription());
    }

    @Test
    void mapRequestToOutgoingDto() {
        Request request = new Request(
                1L,
                "Some request description",
                LocalDateTime.of(2000, 1, 1, 1, 1, 1),
                new User(
                        2L,
                        null,
                        null));
        OutgoingRequestDto result = RequestMapper.mapRequestToOutgoingDto(request);
        assertEquals(request.getId(), result.getId());
        assertEquals(request.getDescription(), result.getDescription());
        assertEquals(request.getCreated(), result.getCreated());
        assertEquals(request.getRequester().getId(), result.getRequesterId());
    }
}