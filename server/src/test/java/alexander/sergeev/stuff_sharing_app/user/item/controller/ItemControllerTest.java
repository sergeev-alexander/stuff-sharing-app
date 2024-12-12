package alexander.sergeev.stuff_sharing_app.user.item.controller;

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

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.http.HttpHeader.header;

@WebMvcTest({ItemController.class, ExceptionResolver.class})
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemService itemService;
    private final Sort sortByStartDesc = Sort.by(Sort.Direction.DESC, "start");

    @Test
    @SneakyThrows
    void getAllOwnerItems_whenInvoke_shouldInvokeItemServiceMethod() {
        OutgoingItemDto outgoingItemDto = new OutgoingItemDto(
                1L,
                "Item name",
                "Item description",
                true,
                null,
                null,
                List.of(),
                2L);
        when(itemService.getAllOwnerItems(3L, PageRequest.of(0, 20, sortByStartDesc)))
                .thenReturn(List.of(outgoingItemDto));
        mockMvc.perform(get("/items?from=0&size=20")
                        .header(header, 3))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(List.of(outgoingItemDto))));
        verify(itemService).getAllOwnerItems(3L, PageRequest.of(0, 20, sortByStartDesc));
    }

    @Test
    @SneakyThrows
    void getItemById_whenInvoke_shouldInvokeItemServiceMethod() {
        OutgoingItemDto outgoingItemDto = new OutgoingItemDto(
                1L,
                "Item name",
                "Item description",
                true,
                null,
                null,
                List.of(),
                2L);
        when(itemService.getItemDtoById(3L, 1L))
                .thenReturn(outgoingItemDto);
        mockMvc.perform(get("/items/{id}", 1)
                        .header(header, 3))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(outgoingItemDto)));
        verify(itemService).getItemDtoById(3L, 1L);
    }

    @Test
    @SneakyThrows
    void getItemsBySearch_whenInvoke_shouldInvokeItemServiceMethod() {
        OutgoingItemDto outgoingItemDto = new OutgoingItemDto(
                1L,
                "Item name",
                "Item description",
                true,
                null,
                null,
                List.of(),
                2L);
        when(itemService.getItemsBySearch(3L, "SomeText", PageRequest.of(0, 20)))
                .thenReturn(List.of(outgoingItemDto));
        mockMvc.perform(get("/items/search?from=0&size=20&text=SomeText")
                        .header(header, 3))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(List.of(outgoingItemDto))));
        verify(itemService).getItemsBySearch(3L, "SomeText", PageRequest.of(0, 20));
    }

    @Test
    @SneakyThrows
    void postItem_whenInvoke_shouldInvokeItemServiceMethod() {
        IncomingItemDto incomingItemDto = new IncomingItemDto(
                "Some Name",
                "Some description",
                true,
                null);
        OutgoingItemDto outgoingItemDto = new OutgoingItemDto(
                1L,
                "Some Name",
                "Some description",
                true,
                null,
                null,
                List.of(),
                2L);
        when(itemService.postItem(1L, incomingItemDto))
                .thenReturn(outgoingItemDto);
        mockMvc.perform(post("/items")
                        .header(header, 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(incomingItemDto)))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(outgoingItemDto)));
        verify(itemService).postItem(1L, incomingItemDto);
    }

    @Test
    @SneakyThrows
    void postComment_whenInvoke_shouldInvokeItemServiceMethod() {
        IncomingCommentDto incomingCommentDto = new IncomingCommentDto(
                null,
                "Some text");
        OutgoingCommentDto outgoingCommentDto = new OutgoingCommentDto(
                1L,
                "Some text",
                "Some author name",
                LocalDateTime.of(2000, 1, 1, 1, 1, 1));
        when(itemService.postComment(1L, 1L, incomingCommentDto))
                .thenReturn(outgoingCommentDto);
        mockMvc.perform(post("/items/{id}/comment", 1L)
                        .header(header, 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(incomingCommentDto)))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(outgoingCommentDto)));
        verify(itemService).postComment(1L, 1L, incomingCommentDto);
    }

    @Test
    @SneakyThrows
    void patchItemById_whenInvoke_shouldInvokeItemServiceMethod() {
        IncomingItemDto incomingItemDto = new IncomingItemDto(
                "Some name",
                "Some description",
                true,
                1L);
        OutgoingItemDto outgoingItemDto = new OutgoingItemDto(
                1L,
                "Some name",
                "Some description",
                true,
                null,
                null,
                List.of(),
                1L);
        when(itemService.patchItemById(1L, 1L, incomingItemDto))
                .thenReturn(outgoingItemDto);
        mockMvc.perform(patch("/items/{id}", 1L)
                        .header(header, 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(incomingItemDto)))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(outgoingItemDto)));
        verify(itemService).patchItemById(1L, 1L, incomingItemDto);
    }

    @Test
    @SneakyThrows
    void deleteItemById_whenInvoke_shouldInvokeItemServiceMethod() {
        mockMvc.perform(delete("/items/{id}", 1)
                .header(header, 1))
                .andExpect(status().isOk());
        verify(itemService).deleteItemById(1L, 1L);
    }

}
