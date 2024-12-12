package alexander.sergeev.stuff_sharing_app.user.service;

import alexander.sergeev.stuff_sharing_app.user.dto.UserDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {

    List<UserDto> getAllUsers(Pageable pageable);

    UserDto getUserById(Long userId);

    UserDto postUser(UserDto userDto);

    UserDto patchUserById(Long userId, UserDto userDto);

    UserDto deleteUserById(Long userId);

}