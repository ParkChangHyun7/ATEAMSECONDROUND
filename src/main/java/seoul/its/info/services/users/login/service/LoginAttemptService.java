package seoul.its.info.services.users.login.service;

import jakarta.servlet.http.HttpServletRequest;

public interface LoginAttemptService {
	
    boolean isAttemptAllowed(HttpServletRequest request, String userId);

    void recordFailure(HttpServletRequest request, String userId);

    void resetAttempts(HttpServletRequest request, String userId);
}