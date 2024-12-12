package alexander.sergeev.stuff_sharing_app.user.controller;

import alexander.sergeev.stuff_sharing_app.user.dto.UserDto;
import alexander.sergeev.stuff_sharing_app.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/users")
@Validated
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public List<UserDto> getAllUsers(
            HttpServletRequest request,
            @RequestParam(value = "from") Integer firstElement,
            @RequestParam(value = "size") Integer size) {
        log.info("{} {}?{}", request.getMethod(), request.getRequestURI(), request.getQueryString());
        return userService.getAllUsers(PageRequest.of(firstElement / size, size));
    }

    @GetMapping("/{userId}")
    public UserDto getUserById(
            HttpServletRequest request,
            @PathVariable Long userId) {
        log.info("{} {}", request.getMethod(), request.getRequestURI());
        return userService.getUserById(userId);
    }

    @PostMapping
    public UserDto postUser(
            HttpServletRequest request,
            @RequestBody UserDto userDto) {
        log.info("{} {} {}", request.getMethod(), request.getRequestURI(), userDto);
        return userService.postUser(userDto);
    }

    @PatchMapping("/{userId}")
    public UserDto patchUserById(
            HttpServletRequest request,
            @PathVariable Long userId,
            @RequestBody UserDto userDto) {
        log.info("{} {} {}", request.getMethod(), request.getRequestURI(), userDto);
        return userService.patchUserById(userId, userDto);
    }

    @DeleteMapping("/{userId}")
    public UserDto deleteUserById(
            HttpServletRequest request,
            @PathVariable Long userId) {
        log.info("{} {}", request.getMethod(), request.getRequestURI());
        return userService.deleteUserById(userId);
    }
}