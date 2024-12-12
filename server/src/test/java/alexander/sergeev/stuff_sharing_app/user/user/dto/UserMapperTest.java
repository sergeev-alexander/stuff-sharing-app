package alexander.sergeev.stuff_sharing_app.user.user.dto;

import alexander.sergeev.stuff_sharing_app.user.dto.UserDto;
import alexander.sergeev.stuff_sharing_app.user.dto.UserMapper;
import alexander.sergeev.stuff_sharing_app.user.model.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserMapperTest {

    private final User user = new User(
            1L,
            "Some name",
            "some@email.com");

    private final UserDto userDto = new UserDto(
            1L,
            "Some name",
            "some@email.com");

    @Test
    void mapUserToDto() {
        UserDto result = UserMapper.mapUserToDto(user);
        assertEquals(userDto, result);
    }

    @Test
    void mapDtoToUser() {
        User result = UserMapper.mapDtoToUser(userDto);
        assertEquals(user, result);
    }
}