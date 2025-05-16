package seoul.its.info.services.users.validation.email.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailVerificationConfirmRequest {

    @NotBlank(message = "타입을 입력해주세요.")
    @Pattern(regexp = "^email$", message = "타입은 'email' 이어야 합니다.")
    private String type;

    @NotBlank(message = "이메일 주소를 입력해주세요.")
    @Email(message = "유효한 이메일 형식이 아닙니다.")
    private String value; // 이메일 주소

    @NotBlank(message = "인증 코드를 입력해주세요.")
    @Size(min = 5, max = 5, message = "인증 코드는 5자리여야 합니다.")
    @Pattern(regexp = "^\\d{5}$", message = "인증 코드는 5자리 숫자여야 합니다.")
    private String code;
} 