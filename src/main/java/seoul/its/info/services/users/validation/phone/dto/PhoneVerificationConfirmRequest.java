package seoul.its.info.services.users.validation.phone.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PhoneVerificationConfirmRequest {

    @NotBlank(message = "타입을 입력해주세요.")
    @Pattern(regexp = "^phone$", message = "타입은 'phone' 이어야 합니다.")
    private String type;

    @NotBlank(message = "휴대폰 번호를 입력해주세요.")
    @Pattern(regexp = "^010\\d{8}$", message = "휴대폰 번호 형식이 올바르지 않습니다. (010XXXXXXXX)")
    private String value; // 핸드폰 번호 ('-' 제외)

    @NotBlank(message = "인증 코드를 입력해주세요.")
    @Size(min = 5, max = 5, message = "인증 코드는 5자리여야 합니다.")
    @Pattern(regexp = "^\\d{5}$", message = "인증 코드는 5자리 숫자여야 합니다.")
    private String code;
} 