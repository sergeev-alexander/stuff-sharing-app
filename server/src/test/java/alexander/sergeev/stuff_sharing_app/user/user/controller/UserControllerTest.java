package alexander.sergeev.stuff_sharing_app.user.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({UserController.class, ExceptionResolver.class})
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @Test
    @SneakyThrows
    void getAllUsers_whenInvoke_shouldInvokeUserServiceMethod_andReturnUser() {
        UserDto userDto = new UserDto(
                null,
                "Name",
                "name@email.com");
        when(userService.getAllUsers(PageRequest.of(0, 20)))
                .thenReturn(List.of(userDto));
        mockMvc.perform(get("/users?from=0&size=20"))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(List.of(userDto))));
        verify(userService).getAllUsers(PageRequest.of(0, 20));
    }

    @Test
    @SneakyThrows
    void getUserById_whenValidId_shouldInvokeUserServiceMethod() {
        UserDto userDto = new UserDto(
                1L,
                "Name",
                "name@email.com");
        when(userService.getUserById(1L))
                .thenReturn(userDto);
        mockMvc.perform(get("/users/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(userDto)));
        verify(userService).getUserById(1L);
    }

    @Test
    @SneakyThrows
    void postUser_whenInvoke_shouldInvokeUserServiceMethod() {
        UserDto userDto = new UserDto(
                null,
                "Name",
                "name@email.com");
        when(userService.postUser(userDto)).thenReturn(userDto);
        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(userDto)));
        verify(userService).postUser(userDto);
    }

    @Test
    @SneakyThrows
    void patchUserById_whenInvoke_shouldInvokeUserServiceMethod() {
        UserDto userDto = new UserDto(
                null,
                "Name",
                "name@email.com");
        when(userService.patchUserById(1L, userDto))
                .thenReturn(userDto);
        mockMvc.perform(patch("/users/{id}", 1)
                        .content(objectMapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(userDto)));
        verify(userService).patchUserById(1L, userDto);
    }

    @Test
    @SneakyThrows
    void deleteUserById_whenValidUserId_shouldInvokeUserServiceMethod() {
        mockMvc.perform(delete("/users/{id}", 1))
                .andExpect(status().isOk());
        verify(userService, times(1)).deleteUserById(1L);
    }

}
