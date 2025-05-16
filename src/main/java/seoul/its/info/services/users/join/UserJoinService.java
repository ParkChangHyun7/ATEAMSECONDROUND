package seoul.its.info.services.users.join;

import java.util.Map;

import jakarta.servlet.http.HttpSession;

public interface UserJoinService {
    Map<String, Object> joinConfirm(UserJoinDto userJoinDto, HttpSession session);
}