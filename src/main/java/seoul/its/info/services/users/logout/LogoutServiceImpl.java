package seoul.its.info.services.users.logout;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class LogoutServiceImpl implements LogoutService {

    @Override
    public void logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            String sessionId = session.getId();
            log.info("로그아웃 처리 시작: SessionId={}", sessionId);
            session.removeAttribute("loginUser");
            session.removeAttribute("idx");
            session.removeAttribute("nickname");
            session.removeAttribute("role");
            session.removeAttribute("lastLoginUserActtivityTime");
            // session.invalidate(); // 필요시 세션 자체를 무효화
            log.info("세션 속성 제거 완료. 로그아웃 처리 종료: SessionId={}", sessionId);
        } else {
            log.warn("로그아웃 요청에 대한 세션이 존재하지 않습니다.");
        }
    }
}