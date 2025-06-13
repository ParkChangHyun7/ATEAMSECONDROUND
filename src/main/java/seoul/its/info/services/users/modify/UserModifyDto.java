package seoul.its.info.services.users.modify;

import lombok.Getter;
import lombok.Setter;
import seoul.its.info.services.users.validation.UserValidationDto;
import java.time.LocalDateTime;

@Getter
@Setter
public class UserModifyDto extends UserValidationDto {
    private String login_id;
    private String name;
    private String nickname;
    private String email;
    private String phone_number;
    private String address_postcode;
    private String address_base;
    private String address_detail;
    private String gender;
    private String new_password; // 새 비밀번호(변경 시에만 사용)
    private String birth;
    private boolean agreement_age;
    private boolean agreement_service;
    private boolean agreement_privacy;
    private boolean agreement_alba;
    private boolean agreement_marketing;
    private boolean agreement_benefits;
    private LocalDateTime nickname_changed_at;
} 