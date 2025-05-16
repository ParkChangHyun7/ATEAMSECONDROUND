package seoul.its.info.services.users.login.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import seoul.its.info.services.users.login.dto.UserLoginResponseDto;
import seoul.its.info.services.users.login.service.LoginAttemptService;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class LoginFailureHandler implements AuthenticationFailureHandler {

    private final ObjectMapper objectMapper;
    private final LoginAttemptService loginAttemptService;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException exception)
            throws IOException, ServletException {

        String loginId = request.getParameter("login_id");
        log.warn("로그인 실패 핸들러 실행: 사용자 시도 ID={}, 실패 원인={}", loginId, exception.getClass().getSimpleName());

        loginAttemptService.recordFailure(request, loginId);

        String errorMessage = "아이디 또는 비밀번호를 확인해주세요.";
        HttpStatus httpStatus = HttpStatus.UNAUTHORIZED;

        if (exception instanceof BadCredentialsException) {
            errorMessage = "아이디 또는 비밀번호가 일치하지 않습니다.";
        } else if (exception instanceof LockedException) {
            errorMessage = "계정이 잠겼습니다. 관리자에게 문의하세요.";
        } else if (exception instanceof DisabledException) {
            errorMessage = "계정이 비활성화되었습니다. 관리자에게 문의하세요.";
        } else if (exception instanceof AccountExpiredException) {
            errorMessage = "계정이 만료되었습니다. 관리자에게 문의하세요.";
        } else if (exception instanceof CredentialsExpiredException) {
            errorMessage = "비밀번호가 만료되었습니다. 비밀번호를 변경해주세요.";
        } else {
            errorMessage = "로그인 처리 중 알 수 없는 오류가 발생했습니다.";
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        UserLoginResponseDto responseDto = new UserLoginResponseDto();
        responseDto.setLoginUser(false);
        responseDto.setMessage(errorMessage);
        responseDto.setPasswordUpdateRequired(false);

        response.setStatus(httpStatus.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        objectMapper.writeValue(response.getWriter(), responseDto);
        log.debug("로그인 실패 JSON 응답 전송: {}", responseDto);
    }
}
