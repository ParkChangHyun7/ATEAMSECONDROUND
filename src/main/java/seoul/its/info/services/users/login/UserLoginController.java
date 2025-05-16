package seoul.its.info.services.users.login;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import org.springframework.beans.factory.annotation.Value;
// import com.google.api.client.util.Value; // 이 임포트를 주석 처리하거나 제거합니다.

import lombok.extern.slf4j.Slf4j;
import seoul.its.info.services.users.login.detail.UserDetailsImpl;
import seoul.its.info.services.users.login.dto.UserLoginRequestDto;
import seoul.its.info.services.users.login.service.UserLoginService;

@Slf4j
@Controller
@RequiredArgsConstructor
public class UserLoginController {

    private final UserLoginService userLoginService;
    private final UserLoginMapper userLoginMapper;

    @Value("${user.password.update-recommend-days:180}") // 비밀번호 변경 권장일 주입
    private long passwordUpdateRecommendDays;

    @GetMapping("/user/login")
    public String loginGet(Model model, HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            log.info("loginGet: 사용자 인증되지 않음. 로그인 페이지 표시.");
            model.addAttribute("pageTitle", "로그인");
            model.addAttribute("contentPage", "content_pages/users/login/login.jsp");
            model.addAttribute("scriptsPage", "include/users/login/scripts.jsp");
            model.addAttribute("resourcesPage", "include/users/login/resources.jsp");
            return "base";
        } else {
            String contextPath = request.getContextPath();
            if (contextPath == null || contextPath.isEmpty()) {
                contextPath = "/";
            }
            log.info("loginGet: 사용자 인증됨. 홈으로 리다이렉트.");
            return "redirect:" + contextPath;
        }
    }

    @PostMapping("/user/login")
    public String loginPost(@ModelAttribute UserLoginRequestDto loginDto, HttpServletRequest request, Model model) {
        boolean valid = userLoginService.validateUser(loginDto);

        if (valid) {
            return "redirect:/"; // 홈으로 리다이렉트
        } else {
            // 로그인 실패 시 에러 메세지 전달
            model.addAttribute("error", "아이디 또는 비밀번호가 틀렸습니다.");
            return "user/login"; // 다시 로그인 페이지
        }
    }

    // 로그인 성공 후처리 GET 엔드포인트
    @GetMapping("/users/login/post-success-check")
    @ResponseBody
    public Map<String, Object> postLoginSuccessCheck(Authentication authentication) {
        log.info("로그인 성공 후처리 엔드포인트 호출");

        Map<String, Object> response = new HashMap<>();

        if (authentication != null && authentication.getPrincipal() instanceof UserDetailsImpl) {
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            String loginId = userDetails.getUsername();
            log.debug("인증된 사용자 정보 확인: 사용자 ID={}", loginId);

            // DB에서 사용자 정보 조회하여 비밀번호 업데이트 날짜 확인 (LoginSuccessHandler 로직 재활용)
            Map<String, Object> userInfo = userLoginMapper.findUserById(loginId);
            LocalDateTime passwordUpdatedAt = null;
            if (userInfo != null && userInfo.get("password_updated_at") instanceof LocalDateTime) {
                passwordUpdatedAt = (LocalDateTime) userInfo.get("password_updated_at");
            }

            boolean passwordUpdateRequired = determinePasswordUpdateRequired(passwordUpdatedAt);

            response.put("success", true);
            response.put("loginUser", true); // 로그인 성공을 나타냄
            response.put("nickname", userDetails.getNickname());
            response.put("passwordUpdateRequired", passwordUpdateRequired);
            response.put("message", "로그인 후처리 정보"); // 필요시 메시지 추가
            log.debug("로그인 후처리 응답 데이터: {}", response);

        } else {
            // 인증 정보가 없거나 예상과 다른 경우
            response.put("success", false);
            response.put("message", "로그인 상태 정보 불일치");
            log.warn("로그인 후처리 엔드포인트 호출 시 인증 정보 없음 또는 불일치");
        }

        return response; // Map이 JSON으로 변환되어 응답됩니다.
    }

    @GetMapping("/usercheck")
    @ResponseBody
    public Map<String, Object> checkUserAuthentication(Authentication authentication) {
        log.info("/usercheck 엔드포인트 호출됨");
        Map<String, Object> response = new HashMap<>();

        if (authentication != null && authentication.isAuthenticated()
                && !"anonymousUser".equals(authentication.getPrincipal())) {
            log.debug("/usercheck: 사용자가 인증됨");
            String nickname = ((UserDetailsImpl) authentication.getPrincipal()).getNickname();
            response.put("nickname", nickname);
            response.put("success", true);
        } else {
            log.debug("/usercheck: 사용자가 인증되지 않음");
            response.put("success", false);
        }

        return response;
    }

    @GetMapping("/user/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        log.info("/user/logout 엔드포인트 호출됨");
        if (authentication != null) {
            new SecurityContextLogoutHandler().logout(request, response, authentication);
            log.info("사용자 로그아웃 처리 완료: {}", authentication.getName());
        }
        return "redirect:/"; // 메인 페이지로 리다이렉트
    }

    // LoginSuccessHandler에 있던 비밀번호 업데이트 필요 여부 판단 로직
    private boolean determinePasswordUpdateRequired(LocalDateTime passwordUpdatedAt) {
        if (passwordUpdatedAt != null) {
            long daysSinceLastUpdate = ChronoUnit.DAYS.between(passwordUpdatedAt, LocalDateTime.now());
            // log.debug("비밀번호 마지막 업데이트 {}일 경과. 권장일: {}", daysSinceLastUpdate,
            // passwordUpdateRecommendDays);
            return daysSinceLastUpdate >= passwordUpdateRecommendDays;
        }
        // passwordUpdatedAt 정보가 없으면 (예: 초기 사용자) 업데이트 불필요로 간주
        // log.debug("비밀번호 업데이트 날짜 정보 없음. 업데이트 불필요로 간주.");
        return false;
    }

}