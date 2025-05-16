package seoul.its.info.services.users.validation.email;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import jakarta.servlet.http.HttpSession;
import seoul.its.info.services.users.validation.email.dto.EmailVerificationConfirmRequest;
import seoul.its.info.services.users.validation.email.dto.EmailVerificationRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.validation.annotation.Validated;

@RestController
@RequestMapping("/user/verification")
public class EmailVerificationController {

    @Autowired
    private EmailVerificationService emailVerificationService;

    private static final String EMAIL_VERIFICATION_SESSION_KEY_PREFIX = "emailVerificationData_";
    private static final int CODE_LENGTH = 5;
    private static final int EXPIRATION_MINUTES = 3;
    private static final int RESEND_LIMIT_SECONDS = 5; // 1분 재전송 제한

    @PostMapping("/email-verify-send")
    public ResponseEntity<Map<String, String>> sendVerificationCode(
            @Validated @RequestBody EmailVerificationRequest request,
            HttpSession session) {
        String email = request.getValue();
        String sessionKey = EMAIL_VERIFICATION_SESSION_KEY_PREFIX + email;
        LocalDateTime now = LocalDateTime.now();

        @SuppressWarnings("unchecked")
        Map<String, Object> existingData = (Map<String, Object>) session.getAttribute(sessionKey);

        // 1분 이내 재전송 방지 (Rate Limiting)
        if (existingData != null && existingData.containsKey("lastSent")) {
            LocalDateTime lastSent = (LocalDateTime) existingData.get("lastSent");
            if (Duration.between(lastSent, now).getSeconds() < RESEND_LIMIT_SECONDS) {
                long remainingSeconds = RESEND_LIMIT_SECONDS - Duration.between(lastSent, now).getSeconds();
                return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                        .body(Map.of("message", "너무 자주 요청했습니다. " + remainingSeconds + "초 후에 다시 시도해주세요."));
            }
        }

        String verificationCode = generateVerificationCode();

        // 세션에 인증 정보 저장 (코드, 생성/전송 시각, 이메일)
        Map<String, Object> verificationData = new HashMap<>();
        verificationData.put("code", verificationCode);
        verificationData.put("timestamp", now);
        verificationData.put("email", email);
        verificationData.put("lastSent", now);

        session.setAttribute(sessionKey, verificationData);

        emailVerificationService.sendVerificationEmail(email, verificationCode);
        return ResponseEntity.ok(Map.of("message", "인증 코드가 이메일로 발송되었습니다."));
    }

    @PostMapping("/email-verify-confirm")
    public ResponseEntity<Map<String, String>> verifyEmailCode(
            @Validated @RequestBody EmailVerificationConfirmRequest request,
            HttpSession session) {
        String email = request.getValue();
        String inputCode = request.getCode();
        String sessionKey = EMAIL_VERIFICATION_SESSION_KEY_PREFIX + email;

        @SuppressWarnings("unchecked")
        Map<String, Object> verificationData = (Map<String, Object>) session.getAttribute(sessionKey);

        // 1. 세션 데이터 존재 및 이메일 일치 확인
        if (verificationData == null || !email.equals(verificationData.get("email"))) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "인증번호 요청 기록이 없거나 만료되었습니다. 다시 요청해주세요."));
        }

        // 2. 시간 만료 확인 (3분)
        LocalDateTime creationTimestamp = (LocalDateTime) verificationData.get("timestamp");
        if (Duration.between(creationTimestamp, LocalDateTime.now()).toMinutes() >= EXPIRATION_MINUTES) {
            session.removeAttribute(sessionKey); // 만료 시 세션 데이터 삭제
            return ResponseEntity.badRequest().body(Map.of("message", "인증 시간이 만료되었습니다."));
        }

        // 3. 코드 일치 확인
        String storedCode = (String) verificationData.get("code");
        if (storedCode != null && storedCode.equals(inputCode)) {
            session.removeAttribute(sessionKey); // 성공 시 세션 데이터 삭제
            return ResponseEntity.ok(Map.of("message", "인증 성공"));
        } else {
            return ResponseEntity.badRequest().body(Map.of("message", "인증번호가 일치하지 않습니다."));
        }
    }

    private String generateVerificationCode() {
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < CODE_LENGTH; i++) {
            code.append(random.nextInt(10));
        }
        return code.toString();
    }
}
