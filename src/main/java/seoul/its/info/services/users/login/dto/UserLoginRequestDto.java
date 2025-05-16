package seoul.its.info.services.users.login.dto;

import org.hibernate.validator.constraints.Length;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
@Validated
public class UserLoginRequestDto {
    @NotEmpty
    // 아이디는 빈 값이면 안 됨 (NotBlank)
    @NotBlank(message = "아이디는 필수입니다.")
    @Length(min = 6, max = 20, message = "아이디는 6~20자 이하여야 합니다")
    public String loginId;

    @NotEmpty
    //  비밀번호도 빈 값이면 안 됨
    @NotBlank(message = "비밀번호는 필수입니다.")
    @Length(min = 8, max = 50, message = "비밀번호는 8자 이상 50자 이하이어야 합니다.")
    public String password;
}
