package seoul.its.info.services.users.modify;

import jakarta.servlet.http.HttpSession;
import java.util.Map;

public interface UserModifyService {
    Map<String, Object> getUserInfo(String loginId);
    boolean checkPassword(String loginId, String password);
    Map<String, Object> modifyUserInfo(UserModifyDto dto, HttpSession session);
} 