package alexander.sergeev.stuff_sharing_app.request;

import alexander.sergeev.stuff_sharing_app.exception.ExceptionResolver;
import alexander.sergeev.stuff_sharing_app.request.dto.IncomingRequestDto;
import alexander.sergeev.stuff_sharing_app.request.dto.OutgoingRequestDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;

import javax.validation.ConstraintViolationException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static alexander.sergeev.stuff_sharing_app.http.HttpHeader.header;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({RequestController.class, ExceptionResolver.class})
class RequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RequestClient requestClient;

    private final Sort sortByCreatingDesc = Sort.by(Sort.Direction.DESC, "created");

    private OutgoingRequestDto outgoingRequestDto;

    @BeforeEach
    void setOutgoingRequestDto() {
        outgoingRequestDto = new OutgoingRequestDto(
                1L,
                "Some description",
                LocalDateTime.of(2000, 1, 1, 1, 1, 1),
                1L,
                null);
    }

    @Test
    @SneakyThrows
    void getAllRequesterRequests_whenInvoke_shouldInvokeRequestServiceMethod() {
        when(requestClient.getAllRequesterRequests(1L, 0, 20))
                .thenReturn(new ResponseEntity<>(List.of(outgoingRequestDto), HttpStatus.OK));
        mockMvc.perform(get("/requests")
                        .header(header, 1))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(List.of(outgoingRequestDto))));
        verify(requestClient).getAllRequesterRequests(1L, 0, 20);
    }

    @Test
    @SneakyThrows
    void getAllRequesterRequests_whenWrongHeader_shouldNotInvokeRequestServiceMethod_andThrowMissingRequestHeaderException() {
        mockMvc.perform(get("/requests")
                        .header("WRONG-HEADER", "WRONG-VALUE"))
                .andExpect(status().isBadRequest())
                .andExpect(result ->
                        assertInstanceOf(MissingRequestHeaderException.class, result.getResolvedException()))
                .andExpect(result -> assertEquals("Required request header 'X-Sharer-User-Id' " +
                                "for method parameter type Long is not present",
                        result.getResolvedException().getMessage()))
                .andExpect(content().string("MissingRequestHeaderException : " +
                        "Required request header 'X-Sharer-User-Id' for method parameter " +
                        "type Long is not present"));
        verifyNoInteractions(requestClient);
    }

    @Test
    @SneakyThrows
    void getAllRequesterRequests_whenNotValidFromAndSize_shouldThrowConstraintViolationException() {
        mockMvc.perform(get("/requests?from=-1&size=0")
                        .header(header, 1))
                .andExpect(status().isBadRequest())
                .andExpect(result ->
                        assertInstanceOf(ConstraintViolationException.class, result.getResolvedException()))
                .andExpect(result -> assertTrue(result
                        .getResolvedException().getMessage()
                        .contains("getAllRequesterRequests.size: must be greater than or equal to 1")))
                .andExpect(result -> assertTrue(result
                        .getResolvedException().getMessage()
                        .contains("getAllRequesterRequests.from: must be greater than or equal to 0")))
                .andExpect(content().string(containsString("error\":\"must be greater than or equal")));
        verifyNoInteractions(requestClient);
    }

    @Test
    @SneakyThrows
    void getAllRequests_whenInvoke_shouldInvokeRequestServiceMethod() {
        when(requestClient.getAllRequests(1L, 0, 20))
                .thenReturn(new ResponseEntity<>(List.of(outgoingRequestDto), HttpStatus.OK));
        mockMvc.perform(get("/requests/all")
                        .header(header, 1))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(List.of(outgoingRequestDto))));
        verify(requestClient).getAllRequests(1L, 0, 20);
    }

    @Test
    @SneakyThrows
    void getAllRequests_whenNotValidFromAndSize_shouldThrowConstraintViolationException() {
        mockMvc.perform(get("/requests/all?from=-1&size=0")
                        .header(header, 1))
                .andExpect(status().isBadRequest())
                .andExpect(result ->
                        assertInstanceOf(ConstraintViolationException.class, result.getResolvedException()))
                .andExpect(result -> assertTrue(result
                        .getResolvedException().getMessage()
                        .contains("getAllRequests.size: must be greater than or equal to 1")))
                .andExpect(result -> assertTrue(result
                        .getResolvedException().getMessage()
                        .contains("getAllRequests.from: must be greater than or equal to 0")))
                .andExpect(content().string(containsString("must be greater than or equal")));
        verifyNoInteractions(requestClient);
    }

    @Test
    @SneakyThrows
    void getAllRequests_whenNoRequestHeader_shouldThrowMissingRequestHeaderException() {
        mockMvc.perform(get("/requests/all?from=0&size=1"))
                .andExpect(status().isBadRequest())
                .andExpect(result ->
                        assertInstanceOf(MissingRequestHeaderException.class, result.getResolvedException()))
                .andExpect(result -> assertEquals(
                        "Required request header 'X-Sharer-User-Id' " +
                                "for method parameter type Long is not present",
                        result.getResolvedException().getMessage()))
                .andExpect(content().string("MissingRequestHeaderException : " +
                        "Required request header 'X-Sharer-User-Id' for method parameter " +
                        "type Long is not present"));
        verifyNoInteractions(requestClient);
    }

    @Test
    @SneakyThrows
    void getRequestById_whenInvoke_shouldInvokeRequestServiceMethod() {
        when(requestClient.getRequestById(1L, 1L))
                .thenReturn(new ResponseEntity<>(outgoingRequestDto, HttpStatus.OK));
        mockMvc.perform(get("/requests/{id}", 1)
                        .header(header, 1))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(outgoingRequestDto)));
        verify(requestClient).getRequestById(1L, 1L);
    }

    @Test
    @SneakyThrows
    void getRequestById_whenNegativeRequestId_shouldThrowConstraintViolationException() {
        mockMvc.perform(get("/requests/{id}", -1)
                        .header(header, 1))
                .andExpect(status().isBadRequest())
                .andExpect(result ->
                        assertInstanceOf(ConstraintViolationException.class, result.getResolvedException()))
                .andExpect(result -> assertEquals("getRequestById.requestId: must be greater than 0",
                        result.getResolvedException().getMessage()))
                .andExpect(content().string("{\"error\":\"must be greater than 0\"}"));
        verifyNoInteractions(requestClient);
    }

    @Test
    @SneakyThrows
    void postRequest_whenInvoke_shouldInvokeRequestServiceMethod() {
        IncomingRequestDto incomingRequestDto = new IncomingRequestDto(
                null,
                "Some description");
        when(requestClient.postRequest(1L, incomingRequestDto))
                .thenReturn(new ResponseEntity<>(outgoingRequestDto, HttpStatus.OK));
        mockMvc.perform(post("/requests")
                        .header(header, 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(incomingRequestDto)))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(outgoingRequestDto)));
        verify(requestClient).postRequest(1L, incomingRequestDto);
    }

    @Test
    @SneakyThrows
    void postRequest_whenNotValidRequest_shouldThrowMethodArgumentNotValidException() {
        IncomingRequestDto incomingRequestDto = new IncomingRequestDto(
                1L,
                "");
        mockMvc.perform(post("/requests")
                        .header(header, 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(incomingRequestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(result ->
                        assertInstanceOf(MethodArgumentNotValidException.class, result.getResolvedException()))
                .andExpect(content().string(containsString("id\":\"" +
                        "Creating request already has an id!")))
                .andExpect(content().string(containsString("description\":\"" +
                        "Creating request description field is blank!")));
        verifyNoInteractions(requestClient);
    }

    @Test
    @SneakyThrows
    void postRequest_whenWrongRequestHeader_shouldThrowMissingRequestHeaderException() {
        IncomingRequestDto incomingRequestDto = new IncomingRequestDto(
                null,
                "Some description");
        mockMvc.perform(post("/requests")
                        .header("WRONG-HEADER", 123)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(incomingRequestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(result ->
                        assertInstanceOf(MissingRequestHeaderException.class, result.getResolvedException()))
                .andExpect(result -> assertEquals(
                        "Required request header 'X-Sharer-User-Id' " +
                                "for method parameter type Long is not present",
                        result.getResolvedException().getMessage()))
                .andExpect(content().string("MissingRequestHeaderException : " +
                        "Required request header 'X-Sharer-User-Id' for method parameter " +
                        "type Long is not present"));
        verifyNoInteractions(requestClient);
    }
}