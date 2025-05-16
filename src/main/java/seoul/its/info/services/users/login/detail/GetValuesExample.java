package seoul.its.info.services.users.login.detail;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import lombok.extern.slf4j.Slf4j;

// ... 다른 코드 ...
@Slf4j
public class GetValuesExample {

    public void someMethod() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            // 유저 인증 정보가 null이 아니면서 로그인 성공한 유저일 경우
            Object principal = authentication.getPrincipal();
            // princiapl을 오브젝트 객체로 선언해서 getPrincipal()로 정보를 받아옴
            if (principal instanceof UserDetailsImpl) {
                // 이 정보가 UserDetailsImpl 클래스로 형변환(캐스팅) 가능한지 if로 확인하고 boolean
                UserDetailsImpl userDetails = (UserDetailsImpl) principal;
                // userDetails에 저장함.

                Long userId = userDetails.getId();
                String loginId = userDetails.getUsername(); // 또는 userDetails.getUsername() (UserDetails 인터페이스의 표준 메서드)
                String nickname = userDetails.getNickname();
                Integer role = userDetails.getRole();

                log.info("현재 로그인한 사용자 ID: {}", userId);
                log.info("현재 로그인한 사용자 로그인 ID: {}", loginId);
                log.info("현재 로그인한 사용자 닉네임: {}", nickname);
                log.info("현재 로그인한 사용자 권한: {}", role);

                // 이제 가져온 정보로 원하는 작업을 수행하시면 됩니다!
            } else if (principal instanceof String && principal.equals("anonymousUser")) {
                // 익명 사용자인 경우의 처리
                log.info("현재 사용자는 익명 사용자입니다.");
            } else {
                // 예상치 못한 Principal 타입인 경우
                log.warn("예상치 못한 Principal 타입입니다: {}", principal.getClass().getName());
            }
        } else {
            // 인증되지 않은 사용자의 경우
            log.info("인증된 사용자가 없습니다.");
        }
    }
}

// 컨트롤러에서는 아래 어노테이션 방법으로도 가져올 수 있다고 함
// @AuthenticationPrincipal 하지만! 이 방식은 이미 인증된(로그인 된) 사용자의 정보를 가져오는 방식으로
// 자동 형변환을 도와주는 방식이라 로그인에서나 인증 단계에서는 사용하지 않는다고 함
// "현재 로그인된 사용자가 누구인가?"를 위한 것이지 "누가 로그인 하려고 한다" 용도가 아니라고 함
// 테스트로 써보니 Deprecated임... 사스가 챗지피티...

// import org.springframework.security.core.annotation.AuthenticationPrincipal;
// import org.springframework.stereotype.Controller;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.ResponseBody;
// // UserDetailsImpl import 필요

// @Controller
// public class MyController {

// @GetMapping("/my-info")
// @ResponseBody
// public String getMyInfo(@AuthenticationPrincipal UserDetailsImpl userDetails)
// {
// if (userDetails != null) {
// Long userId = userDetails.getId();
// String loginId = userDetails.getLoginId();
// String nickname = userDetails.getNickname();
// Integer role = userDetails.getRole();

// return String.format("ID: %d, 로그인 ID: %s, 닉네임: %s, 권한: %d", userId, loginId,
// nickname, role);
// } else {
// return "로그인한 사용자가 없습니다.";
// }
// }
// }
