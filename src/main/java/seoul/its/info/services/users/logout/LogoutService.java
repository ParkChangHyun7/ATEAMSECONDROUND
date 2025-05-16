package seoul.its.info.services.users.logout;

import jakarta.servlet.http.HttpServletRequest;

public interface LogoutService {
    void logout(HttpServletRequest request);
}