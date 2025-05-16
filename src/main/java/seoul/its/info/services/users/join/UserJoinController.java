package seoul.its.info.services.users.join;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.springframework.ui.Model;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
public class UserJoinController {

    private final UserJoinService userJoinService;

    @GetMapping("/user/join")
    public String joinGetRequest(Model model) {
        model.addAttribute("pageTitle", "회원가입");
        model.addAttribute("contentPage", "content_pages/users/join/join.jsp");
        model.addAttribute("resourcesPage", "include/users/join/resources.jsp");
        model.addAttribute("scriptsPage", "include/users/join/scripts.jsp");
        return "base";
    }

    @PostMapping("/joinConfirm")
    public ResponseEntity<?> joinPostRequest(
            @RequestBody UserJoinDto uj, HttpServletRequest rq) {
        HttpSession hs = rq.getSession();
        log.info("회원가입 요청 수신: 사용자 ID={}", uj.getLogin_id());
        Map<String, Object> result = userJoinService.joinConfirm(uj, hs);
        return ResponseEntity.ok(result);
    }
}