package alexander.sergeev.stuff_sharing_app.user.booking.controller;

import alexander.sergeev.stuff_sharing_app.booking.controller.BookingController;
import alexander.sergeev.stuff_sharing_app.booking.dto.IncomingBookingDto;
import alexander.sergeev.stuff_sharing_app.booking.dto.OutgoingBookingDto;
import alexander.sergeev.stuff_sharing_app.booking.model.BookingState;
import alexander.sergeev.stuff_sharing_app.booking.model.BookingStatus;
import alexander.sergeev.stuff_sharing_app.booking.service.BookingService;
import alexander.sergeev.stuff_sharing_app.exception.ExceptionResolver;
import alexander.sergeev.stuff_sharing_app.item.model.Item;
import alexander.sergeev.stuff_sharing_app.user.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static alexander.sergeev.stuff_sharing_app.http.HttpHeader.header;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
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
    private BookingService bookingService;

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
    void getAllUserBookings_whenInvoke_shouldInvokeServiceMethod_andReturnBookingList() {
        when(bookingService.getAllUserBookings(2L, BookingState.ALL,
                PageRequest.of(0, 20, sortByStartDesc)))
                .thenReturn(List.of(outgoingBookingDto));
        mockMvc.perform(get("/bookings?state=ALL&from=0&size=20")
                        .header(header, 2))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(List.of(outgoingBookingDto))));
        verify(bookingService).getAllUserBookings(2L, BookingState.ALL,
                PageRequest.of(0, 20, sortByStartDesc));
    }

    @Test
    @SneakyThrows
    void getAllOwnerItemBookings_whenInvoke_shouldInvokeServiceMethod_andReturnBookingList() {
        when(bookingService.getAllOwnerItemBookings(1L, BookingState.ALL,
                PageRequest.of(0, 20, sortByStartDesc)))
                .thenReturn(List.of(outgoingBookingDto));
        mockMvc.perform(get("/bookings/owner?state=ALL&from=0&size=20")
                        .header(header, 1))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(List.of(outgoingBookingDto))));
        verify(bookingService).getAllOwnerItemBookings(1L, BookingState.ALL,
                PageRequest.of(0, 20, sortByStartDesc));
    }

    @Test
    @SneakyThrows
    void getBookingById_whenInvoke_shouldInvokeServiceMethod_andReturnBooking() {
        when(bookingService.getBookingById(2L, 1L))
                .thenReturn(outgoingBookingDto);
        mockMvc.perform(get("/bookings/{id}", 1)
                        .header(header, 2))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(outgoingBookingDto)));
        verify(bookingService).getBookingById(2L, 1L);
    }

    @Test
    @SneakyThrows
    void postBooking_whenInvoke_shouldInvokeServiceMethod_andReturnBooking() {
        IncomingBookingDto incomingBookingDto = new IncomingBookingDto(
                null,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                1L);
        when(bookingService.postBooking(2L, incomingBookingDto))
                .thenReturn(outgoingBookingDto);
        mockMvc.perform(post("/bookings")
                        .header(header, 2)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(incomingBookingDto)))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(outgoingBookingDto)));
        verify(bookingService).postBooking(2L, incomingBookingDto);
    }

    @Test
    @SneakyThrows
    void patchBooking_whenInvoke_shouldInvokeServiceMethod_andReturnBooking() {
        when(bookingService.patchBookingById(1L, 1L, true))
                .thenReturn(outgoingBookingDto);
        mockMvc.perform(patch("/bookings/{id}", 1)
                        .header(header, 1)
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(outgoingBookingDto)));
        verify(bookingService).patchBookingById(1L, 1L, true);
    }
}