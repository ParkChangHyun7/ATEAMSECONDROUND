package seoul.its.info.services.users.modify;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.ui.Model;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.security.Principal;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import seoul.its.info.common.security.aesencryptor.AESEncoderDecoder;

@Slf4j
@Controller
@RequiredArgsConstructor
public class UserModifyController {

    private final UserModifyService userModifyService;

    @Autowired
    private AESEncoderDecoder aesEncoderDecoder;

    @GetMapping("/user/modify")
    public String modifyGetRequest(Model model) {
        model.addAttribute("pageTitle", "회원정보 수정");
        model.addAttribute("contentPage", "content_pages/users/modify/modify.jsp");
        model.addAttribute("resourcesPage", "include/users/modify/resources.jsp");
        model.addAttribute("scriptsPage", "include/users/modify/scripts.jsp");
        return "base";
    }

    @GetMapping("/user/modify/info")
    @ResponseBody
    public Map<String, Object> getUserInfo(Principal principal) {
        String loginId = principal != null ? principal.getName() : null;
        log.info("[getUserInfo] loginId: {}", loginId);
        Map<String, Object> result = userModifyService.getUserInfo(loginId);
        log.info("[getUserInfo] result: {}", result);
        return result;
    }

    @PostMapping("/user/modify/password-check")
    @ResponseBody
    public Map<String, Object> checkPassword(@RequestBody Map<String, String> body, Principal principal) {
        String loginId = principal != null ? principal.getName() : null;
        String password = body.get("password");
        log.info("[checkPassword] loginId: {}, password: {}", loginId, password);
        boolean result = userModifyService.checkPassword(loginId, password);
        log.info("[checkPassword] result: {}", result);
        return Map.of("success", result);
    }

    @PostMapping("/user/modify")
    @ResponseBody
    public Map<String, Object> modifyUserInfo(@RequestBody UserModifyDto dto, Principal principal) {
        String loginId = principal != null ? principal.getName() : null;
        log.info("[modifyUserInfo] loginId: {}, dto: {}", loginId, dto);
        Map<String, Object> result = userModifyService.modifyUserInfo(dto, null);
        log.info("[modifyUserInfo] result: {}", result);
        return result;
    }
}
