package alexander.sergeev.stuff_sharing_app.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Null;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    @Null(groups = ValidationMarker.OnCreate.class, message = "Creating user already has an id!")
    private Long id;

    @NotBlank(groups = ValidationMarker.OnCreate.class, message = "User name field is blank!")
    @Size(groups = {ValidationMarker.OnCreate.class, ValidationMarker.OnUpdate.class}, max = 128,
            message = "Creating or updating user name field is bigger than 128 characters!")
    private String name;

    @NotBlank(groups = ValidationMarker.OnCreate.class, message = "Email field is blank!")
    @Email(groups = {ValidationMarker.OnCreate.class, ValidationMarker.OnUpdate.class},
            message = "Wrong email format!")
    @Size(groups = {ValidationMarker.OnCreate.class, ValidationMarker.OnUpdate.class}, max = 128,
            message = "Creating or updating user email field is bigger than 128 characters!")
    private String email;

}
