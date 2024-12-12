package alexander.sergeev.stuff_sharing_app.user.user.dto;

import org.junit.jupiter.api.Test;

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
