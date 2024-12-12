package alexander.sergeev.stuff_sharing_app.booking;

import alexander.sergeev.stuff_sharing_app.booking.dto.IncomingBookingDto;
import alexander.sergeev.stuff_sharing_app.booking.dto.OutgoingBookingDto;
import alexander.sergeev.stuff_sharing_app.booking.model.BookingStatus;
import alexander.sergeev.stuff_sharing_app.exception.ExceptionResolver;
import alexander.sergeev.stuff_sharing_app.item.model.Item;
import alexander.sergeev.stuff_sharing_app.user.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({BookingController.class, ExceptionResolver.class})
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingClient bookingClient;
    private final Sort sortByStartDesc = Sort.by(Sort.Direction.DESC, "start");
    private final OutgoingBookingDto outgoingBookingDto = new OutgoingBookingDto(
            1L,
            LocalDateTime.now().minusDays(1),
            LocalDateTime.now().plusDays(1),
            new User(
                    2L,
                    "Booker name",
                    "booker@email.com"),
            new Item(
                    1L,
                    "Item name",
                    "Item description",
                    true,
                    null,
                    new User(
                            1L,
                            "Owner name",
                            "owner@email.com")),
            BookingStatus.WAITING);

    @Test
    @SneakyThrows
    void getAllUserBookings_whenAllParamsAreValid_shouldInvokeClientMethod_andReturnBookingList() {
        when(bookingClient.getAllUserBookings(2L, "ALL",
                0, 20))
                .thenReturn(new ResponseEntity<>(List.of(outgoingBookingDto), HttpStatus.OK));
        mockMvc.perform(get("/bookings?state=ALL&from=0&size=20")
                        .header(header, 2))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper
                        .writeValueAsString(List.of(outgoingBookingDto))));
        verify(bookingClient).getAllUserBookings(2L, "ALL", 0, 20);
    }

    @Test
    @SneakyThrows
    void getAllUserBookings_whenNotValidRequestHeader_shouldThrowMissingRequestHeaderException() {
        mockMvc.perform(get("/bookings?state=ALL&from=0&size=20")
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
        verifyNoInteractions(bookingClient);
    }

    @Test
    @SneakyThrows
    void getAllUserBookings_whenNotValidFromAndSize_shouldThrowConstraintViolationException() {
        mockMvc.perform(get("/bookings?from=-1&size=0")
                        .header(header, 2))
                .andExpect(status().isBadRequest())
                .andExpect(result ->
                        assertInstanceOf(ConstraintViolationException.class, result.getResolvedException()))
                .andExpect(result -> assertTrue(result
                        .getResolvedException().getMessage()
                        .contains("getAllUserBookings.size: must be greater than or equal to 1")))
                .andExpect(result -> assertTrue(result
                        .getResolvedException().getMessage()
                        .contains("getAllUserBookings.from: must be greater than or equal to 0")))
                .andExpect(content().string(containsString("must be greater than or equal")));
        verifyNoInteractions(bookingClient);
    }

    @Test
    @SneakyThrows
    void getAllUserBookings_whenWrongState_shouldThrowConstraintViolationException() {
        mockMvc.perform(get("/bookings?state=WRONG-STATE")
                        .header(header, 2))
                .andExpect(status().isBadRequest())
                .andExpect(result ->
                        assertInstanceOf(ConstraintViolationException.class, result.getResolvedException()))
                .andExpect(result -> assertEquals("getAllUserBookings.bookingStateString.state: " +
                                "Unknown state: UNSUPPORTED_STATUS",
                        result.getResolvedException().getMessage()))
                .andExpect(content().string("{\"error\":\"Unknown state: UNSUPPORTED_STATUS\"}"));
        verifyNoInteractions(bookingClient);
    }

    @Test
    @SneakyThrows
    void getAllOwnerItemBookings_whenAllParamsAreValid_shouldInvokeClientMethod_andReturnBookingList() {
        when(bookingClient.getAllOwnerItemBookings(1L, "ALL",
                0, 20))
                .thenReturn(new ResponseEntity<>(List.of(outgoingBookingDto), HttpStatus.OK));
        mockMvc.perform(get("/bookings/owner?state=ALL&from=0&size=20")
                        .header(header, 1))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper
                        .writeValueAsString(List.of(outgoingBookingDto))));
        verify(bookingClient).getAllOwnerItemBookings(1L, "ALL", 0, 20);
    }

    @Test
    @SneakyThrows
    void getAllOwnerItemBookings_whenNegativeHeader_shouldThrowConstraintViolationException() {
        mockMvc.perform(get("/bookings/owner?state=ALL&from=0&size=20", -1)
                        .header(header, -1))
                .andExpect(status().isBadRequest())
                .andExpect(result ->
                        assertInstanceOf(ConstraintViolationException.class, result.getResolvedException()))
                .andExpect(result -> assertEquals("getAllOwnerItemBookings.ownerId: must be greater than 0",
                        result.getResolvedException().getMessage()))
                .andExpect(content().string("{\"error\":\"must be greater than 0\"}"));
        verifyNoInteractions(bookingClient);
    }

    @Test
    @SneakyThrows
    void getBookingById_whenAllParamsAreValid_shouldInvokeClientMethod_andReturnBooking() {
        when(bookingClient.getBookingById(2L, 1L))
                .thenReturn(new ResponseEntity<>(outgoingBookingDto, HttpStatus.OK));
        mockMvc.perform(get("/bookings/{id}", 1)
                        .header(header, 2))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper
                        .writeValueAsString(outgoingBookingDto)));
        verify(bookingClient).getBookingById(2L, 1L);
    }

    @Test
    @SneakyThrows
    void getBookingById_whenNegativeBookingId_shouldThrowConstraintViolationException() {
        mockMvc.perform(get("/bookings/{id}", -1)
                        .header(header, 1))
                .andExpect(status().isBadRequest())
                .andExpect(result ->
                        assertInstanceOf(ConstraintViolationException.class, result.getResolvedException()))
                .andExpect(result -> assertEquals("getBookingById.bookingId: must be greater than 0",
                        result.getResolvedException().getMessage()))
                .andExpect(content().string("{\"error\":\"must be greater than 0\"}"));
        verifyNoInteractions(bookingClient);
    }

    @Test
    @SneakyThrows
    void postBooking_whenValidBooking_shouldInvokeClientMethod_andReturnBooking() {
        IncomingBookingDto incomingBookingDto = new IncomingBookingDto(
                null,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                1L);
        when(bookingClient.postBooking(2L, incomingBookingDto))
                .thenReturn(new ResponseEntity<>(outgoingBookingDto, HttpStatus.OK));
        mockMvc.perform(post("/bookings")
                        .header(header, 2)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(incomingBookingDto)))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper
                        .writeValueAsString(outgoingBookingDto)));
        verify(bookingClient).postBooking(2L, incomingBookingDto);
    }

    @Test
    @SneakyThrows
    void postBooking_whenStartIsInPast_shouldThrow_shouldThrowMethodArgumentNotValidException() {
        IncomingBookingDto incomingBookingDto = new IncomingBookingDto(
                null,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().plusDays(1),
                1L);
        mockMvc.perform(post("/bookings")
                        .header(header, 2)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(incomingBookingDto)))
                .andExpect(status().isBadRequest())
                .andExpect(result ->
                        assertInstanceOf(MethodArgumentNotValidException.class, result.getResolvedException()))
                .andExpect(result -> assertTrue(result.getResolvedException().getMessage()
                        .contains("Creating booking start is in past!")))
                .andExpect(content().string(
                        containsString("Creating booking start is in past!")));
        verifyNoInteractions(bookingClient);
    }

    @Test
    @SneakyThrows
    void postBooking_whenEndIsInPast_shouldThrow_shouldThrowMethodArgumentNotValidException() {
        IncomingBookingDto incomingBookingDto = new IncomingBookingDto(
                null,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().minusDays(1),
                1L);
        mockMvc.perform(post("/bookings")
                        .header(header, 2)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(incomingBookingDto)))
                .andExpect(status().isBadRequest())
                .andExpect(result ->
                        assertInstanceOf(MethodArgumentNotValidException.class, result.getResolvedException()))
                .andExpect(result -> assertTrue(result.getResolvedException().getMessage()
                        .contains("Creating booking end is in past!")))
                .andExpect(content().string(
                        containsString("Creating booking end is in past!")));
        verifyNoInteractions(bookingClient);
    }

    @Test
    @SneakyThrows
    void postBooking_whenEndIsBeforeStart_shouldThrow_shouldThrowMethodArgumentNotValidException() {
        IncomingBookingDto incomingBookingDto = new IncomingBookingDto(
                null,
                LocalDateTime.now().plusDays(2),
                LocalDateTime.now().plusDays(1),
                1L);
        mockMvc.perform(post("/bookings")
                        .header(header, 2)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(incomingBookingDto)))
                .andExpect(status().isBadRequest())
                .andExpect(result ->
                        assertInstanceOf(MethodArgumentNotValidException.class, result.getResolvedException()))
                .andExpect(result -> assertTrue(result.getResolvedException().getMessage()
                        .contains("Creating booking start is before end!")))
                .andExpect(content().string("{\"start\":\"Creating booking start is before end!\"}"));
        verifyNoInteractions(bookingClient);
    }

    @Test
    @SneakyThrows
    void patchBooking_whenValidParams_shouldInvokeClientMethod_andReturnBooking() {
        when(bookingClient.patchBooking(1L, 1L, true))
                .thenReturn(new ResponseEntity<>(outgoingBookingDto, HttpStatus.OK));
        mockMvc.perform(patch("/bookings/{id}", 1)
                        .header(header, 1)
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(outgoingBookingDto)));
        verify(bookingClient).patchBooking(1L, 1L, true);
    }

    @Test
    @SneakyThrows
    void patchBooking_whenNegativeBookingId_shouldThrowConstraintViolationException() {
        mockMvc.perform(patch("/bookings/{id}", -1)
                        .header(header, 1)
                        .param("approved", "true"))
                .andExpect(status().isBadRequest())
                .andExpect(result ->
                        assertInstanceOf(ConstraintViolationException.class, result.getResolvedException()))
                .andExpect(result -> assertEquals("patchBooking.bookingId: must be greater than 0",
                        result.getResolvedException().getMessage()))
                .andExpect(content().string("{\"error\":\"must be greater than 0\"}"));
        verifyNoInteractions(bookingClient);
    }
}