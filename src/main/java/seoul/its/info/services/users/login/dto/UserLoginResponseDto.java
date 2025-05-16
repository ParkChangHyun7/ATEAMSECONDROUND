package seoul.its.info.services.users.login.dto;

import lombok.Data;

//현재 사용되지 않고 있는 파일.
@Data
public class UserLoginResponseDto {
    private boolean loginUser;

    // 로그인한 사용자의 닉네임
    private String nickname;
    private boolean passwordUpdateRequired;
    // 로그인 결과에 대한 메시지 (예: 성공, 비밀번호 틀림 등)
    private String message;
}
