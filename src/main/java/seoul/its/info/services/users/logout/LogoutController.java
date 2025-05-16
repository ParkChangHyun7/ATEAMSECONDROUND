package seoul.its.info.services.users.logout;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
@Slf4j
public class LogoutController {

    private final LogoutService logoutService;

    @PostMapping("/user/logout")
    public String logoutPost(HttpServletRequest request) {
        log.info("POST /user/logout 요청 수신");
        logoutService.logout(request);
        return "redirect:/login";
    }
}