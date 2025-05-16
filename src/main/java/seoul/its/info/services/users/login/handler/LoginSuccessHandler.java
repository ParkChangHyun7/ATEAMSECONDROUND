package seoul.its.info.services.users.login.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import seoul.its.info.services.users.login.service.LoginAttemptService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final LoginAttemptService loginAttemptService;

    @Value("${user.password.update-recommend-days:180}")
    private long passwordUpdateRecommendDays;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication)
            throws IOException, ServletException {
        String loginId = authentication.getName();
        log.info("로그인 성공 핸들러 실행: 사용자 ID={}", loginId);

        loginAttemptService.resetAttempts(request, loginId);

        // 새로운 후처리 엔드포인트로 리다이렉트
        String postSuccessCheckUrl = request.getContextPath() + "/"; // 새로운 GET 엔드포인트 URL
        // + "/user/login/post-success-check"; // 새로운 GET 엔드포인트 URL
        response.sendRedirect(postSuccessCheckUrl);

        log.info("로그인 성공 - 후처리 엔드포인트로 리다이렉트: 사용자 ID={}, URL={}", loginId, postSuccessCheckUrl);
    }
}
