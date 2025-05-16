package seoul.its.info.services.users.validation;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ValidationRequestDto {

    @NotBlank(message = "검증 타입(type)은 필수입니다.")
    private String type;

    @NotBlank(message = "검증할 값(value)은 필수입니다.")
    private String value;
}