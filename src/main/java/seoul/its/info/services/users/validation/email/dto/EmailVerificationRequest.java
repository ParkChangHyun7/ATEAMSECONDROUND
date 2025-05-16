package seoul.its.info.services.users.validation.email.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailVerificationRequest {

    @NotBlank(message = "타입을 입력해주세요.")
    @Pattern(regexp = "^email$", message = "타입은 'email' 이어야 합니다.")
    private String type;

    @NotBlank(message = "이메일 주소를 입력해주세요.")
    @Email(message = "유효한 이메일 형식이 아닙니다.")
    private String value; // 이메일 주소
} 