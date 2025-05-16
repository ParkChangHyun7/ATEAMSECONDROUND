package seoul.its.info.services.users.validation;

import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
public class UserValidationDto {

    // --- Validation Groups ---
    public interface IdValidation {
    }

    public interface PasswordValidation {
    }

    public interface NicknameValidation {
    }

    public interface EmailValidation {
    }

    public interface PhoneValidation {
    }

    public interface BirthValidation {
    }

    public interface UserNameValidation {
    }

    // --- Fields ---
    // phone_number (필수)
    @NotBlank(message = "전화번호는 필수입니다.", groups = PhoneValidation.class)
    @Pattern(regexp = "^010\\d{8}$", message = "전화번호 형식이 올바르지 않습니다. (010######## 형식)", groups = PhoneValidation.class)
    @Length(min = 11, max = 11, message = "전화번호는 11자리여야 합니다.", groups = PhoneValidation.class)
    private String phone_number;

    // login_id (필수)
    @NotBlank(message = "아이디는 필수입니다.", groups = IdValidation.class)
    @Length(min = 6, max = 20, message = "아이디는 6~20자 이하여야 합니다", groups = IdValidation.class)
    @Pattern(regexp = "^[a-z][a-z0-9]*$", message = "아이디는 영문 소문자로 시작하고 영문 소문자, 숫자만 사용 가능합니다", groups = IdValidation.class)
    @Pattern(regexp = "^[a-z0-9]*[-_]?[a-z0-9]*$", message = "특수문자는 언더바(_), 대시(-) 중 1회만 사용 가능합니다", groups = IdValidation.class)
    @Pattern(regexp = "^(?!.*(.)\\1{3,}).*$", message = "동일 문자를 4회 이상 연속해서 사용할 수 없습니다", groups = IdValidation.class)
    private String login_id;

    // email (선택)
    @Email(message = "유효한 이메일 형식으로 작성해주세요.", groups = EmailValidation.class)
    @Pattern(regexp = "^[a-zA-Z0-9][a-zA-Z0-9-_]{0,62}[a-zA-Z0-9]@([a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*\\.[a-zA-Z]{2,})$", message = "이메일 형식이 올바르지 않습니다.", groups = EmailValidation.class)
    @Length(max = 100, message = "이메일 주소는 100자를 초과할 수 없습니다.", groups = EmailValidation.class)
    private String email;

    // nickname (필수)
    @NotBlank(message = "닉네임은 필수입니다.", groups = NicknameValidation.class)
    @Length(min = 2, max = 8, message = "닉네임은 2~8자 이하여야 합니다", groups = NicknameValidation.class)
    @Pattern(regexp = "^[가-힣a-zA-Z0-9]+$", message = "닉네임은 한글, 영문, 숫자만 사용 가능합니다", groups = NicknameValidation.class)
    @Pattern(regexp = "^(?![ㄱ-ㅎㅏ-ㅣ]).*$", message = "자음, 모음 단독으로 사용할 수 없습니다", groups = NicknameValidation.class)
    @Pattern(regexp = "^(?!.*(.)\\1{4,}).*$", message = "동일 문자를 5회 이상 연속해서 사용할 수 없습니다", groups = NicknameValidation.class)
    private String nickname;

    // password (필수)
    @NotBlank(message = "비밀번호는 필수입니다.", groups = PasswordValidation.class) // PasswordValidation 그룹 사용
    @Length(min = 8, max = 50, message = "비밀번호는 8자 이상 50자 이하이어야 합니다.", groups = PasswordValidation.class)
    @Pattern(regexp = ".*[A-Z].*", message = "비밀번호는 최소 1개 이상의 대문자를 포함해야 합니다.", groups = PasswordValidation.class)
    @Pattern(regexp = ".*[a-z].*", message = "비밀번호는 최소 1개 이상의 소문자를 포함해야 합니다.", groups = PasswordValidation.class)
    @Pattern(regexp = ".*[0-9].*", message = "비밀번호는 최소 1개 이상의 숫자를 포함해야 합니다.", groups = PasswordValidation.class)
    @Pattern(regexp = ".*[!@#$%^&*].*", message = "비밀번호는 특수문자(!@#$%^&*)를 최소 1개 이상 포함해야 합니다.", groups = PasswordValidation.class)
    @Pattern(regexp = "^(?!.*(.)\\1{2}).*$", message = "동일한 문자를 3회 이상 연속해서 사용할 수 없습니다.", groups = PasswordValidation.class)
    private String password;

    // 이름 (필수)
    @NotBlank(message = "이름은 필수입니다.")
    @Length(min = 2, max = 20, message = "이름은 2~20자 이하여야 합니다", groups = IdValidation.class)
    @Pattern(regexp = "^[가-힣]+$", message = "이름은 한글만 사용 가능합니다", groups = IdValidation.class)
    private String name;

    // birth (선택)
    @Pattern(regexp = "^\\d{4}\\d{2}\\d{2}$", message = "생년월일은 YYYYMMDD 형식이어야 합니다.", groups = BirthValidation.class)
    private String birth;

}