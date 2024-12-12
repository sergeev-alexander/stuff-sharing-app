package alexander.sergeev.stuff_sharing_app.user.dto;

import alexander.sergeev.stuff_sharing_app.user.model.User;
import lombok.experimental.UtilityClass;

@UtilityClass
public class UserMapper {

    public UserDto mapUserToDto(User user) {
        return new UserDto(
                user.getId(),
                user.getName(),
                user.getEmail());
    }

    public User mapDtoToUser(UserDto userDto) {
        return new User(
                userDto.getId(),
                userDto.getName(),
                userDto.getEmail());
    }
}