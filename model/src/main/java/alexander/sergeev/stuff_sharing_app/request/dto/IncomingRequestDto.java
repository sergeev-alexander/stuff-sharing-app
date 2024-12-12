package alexander.sergeev.stuff_sharing_app.request.dto;

import alexander.sergeev.stuff_sharing_app.validation.ValidationMarker;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Null;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IncomingRequestDto {

    @Null(groups = ValidationMarker.OnCreate.class, message = "Creating request already has an id!")
    private Long id;

    @NotBlank(groups = ValidationMarker.OnCreate.class,
            message = "Creating request description field is blank!")
    @Size(groups = ValidationMarker.OnCreate.class, max = 128,
            message = "Creating request description field is bigger than 128 characters!")
    private String description;

}