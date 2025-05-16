package seoul.its.info.services.users.validation.phone;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import seoul.its.info.services.users.validation.phone.dto.PhoneVerificationConfirmRequest;
import seoul.its.info.services.users.validation.phone.dto.PhoneVerificationRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/user/verification")
@RequiredArgsConstructor
public class PhoneVerificationController {

    private final PhoneVerificationService phoneVerificationService;

    // 세션별 마지막 요청 시간 추적 (1분 재전송 제한용)
    private static final Map<String, LocalDateTime> lastRequestTimestamps = new ConcurrentHashMap<>();
    // 세션별 인증 실패 정보 추적 (시도 횟수 제한용)
    private static final Map<String, AttemptInfo> failedAttempts = new ConcurrentHashMap<>();

    private static final String PHONE_VERIFICATION_SESSION_KEY_PREFIX = "phoneVerificationData_";
    private static final int EXPIRATION_MINUTES = 3;
    private static final int RESEND_LIMIT_SECONDS = 60;
    private static final int MAX_FAIL_ATTEMPTS = 3;
    private static final int FAIL_WINDOW_MINUTES = 5;
    private static final int LOCKOUT_MINUTES = 10;

    @PostMapping("/phone-verify-send")
    public ResponseEntity<Map<String, String>> sendVerificationCode(
            @Valid @RequestBody PhoneVerificationRequest request,
            HttpSession session) {
        String sessionId = session.getId();
        LocalDateTime now = LocalDateTime.now();

        // 1. 시도 횟수 초과로 잠겨 있는지 확인
        AttemptInfo attemptInfo = failedAttempts.get(sessionId);
        if (attemptInfo != null && attemptInfo.isLocked(now)) {
            long remainingLockoutSeconds = Duration.between(now, attemptInfo.getLockedUntil()).getSeconds();
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body(Map.of("message", "단 시간의 잦은 인증 시도로 " + remainingLockoutSeconds + "초 뒤 인증 번호 전송이 가능합니다."));
        }

        // 2. 1분 이내 재전송 제한 확인 (세션 기준)
        LocalDateTime lastRequestTime = lastRequestTimestamps.get(sessionId);
        if (lastRequestTime != null && Duration.between(lastRequestTime, now).getSeconds() < RESEND_LIMIT_SECONDS) {
            long remainingSeconds = RESEND_LIMIT_SECONDS - Duration.between(lastRequestTime, now).getSeconds();
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body(Map.of("message", "너무 자주 요청했습니다. " + remainingSeconds + "초 후에 다시 시도해주세요."));
        }

        String phoneNumber = request.getValue();
        String verificationCode = phoneVerificationService.generateVerificationCode();

        // 세션에 인증 정보 저장 (코드, 생성 시각)
        Map<String, Object> verificationData = new ConcurrentHashMap<>(); // 세션 데이터용 맵
        verificationData.put("code", verificationCode);
        verificationData.put("timestamp", now);
        session.setAttribute(PHONE_VERIFICATION_SESSION_KEY_PREFIX + phoneNumber, verificationData);

        try {
            // 오토핫키 스크립트 실행 요청
            phoneVerificationService.sendVerificationCode(phoneNumber, verificationCode);

            // 마지막 요청 시간 업데이트
            lastRequestTimestamps.put(sessionId, now);

            // 실패 기록 초기화 (인증번호를 새로 보냈으므로)
            failedAttempts.remove(sessionId);

            return ResponseEntity.ok(Map.of("message", "인증 코드가 발송되었습니다. (스크립트 실행됨)"));
        } catch (Exception e) {
            System.err.println("오토핫키 스크립트 실행 실패: " + phoneNumber + " - " + e.getMessage());
            // 실패 시 사용자에게 알림 (실제 서비스에서는 로그만 남기거나 다른 처리)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "인증 코드 발송 처리 중 오류가 발생했습니다. 관리자에게 문의해주세요."));
        }
    }

    @PostMapping("/phone-verify-confirm")
    public ResponseEntity<Map<String, String>> verifyPhoneNumber(
            @Valid @RequestBody PhoneVerificationConfirmRequest request,
            HttpSession session) {
        String sessionId = session.getId();
        String phoneNumber = request.getValue();
        String inputCode = request.getCode();
        String sessionKey = PHONE_VERIFICATION_SESSION_KEY_PREFIX + phoneNumber;
        LocalDateTime now = LocalDateTime.now();

        // 1. 시도 횟수 초과로 잠겨 있는지 확인
        AttemptInfo attemptInfo = failedAttempts.get(sessionId);
        if (attemptInfo != null && attemptInfo.isLocked(now)) {
            long remainingLockoutSeconds = Duration.between(now, attemptInfo.getLockedUntil()).getSeconds();
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body(Map.of("message",
                            "단 시간의 잦은 인증 시도로 인해 잠겼습니다. " + remainingLockoutSeconds + "초 후에 다시 시도해주세요."));
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> verificationData = (Map<String, Object>) session.getAttribute(sessionKey);

        // 2. 세션 데이터 존재 확인
        if (verificationData == null) {
            handleFailedAttempt(sessionId, now); // 실패 처리
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "인증번호 요청 기록이 없거나 만료되었습니다. 다시 요청해주세요."));
        }

        // 3. 시간 만료 확인 (3분)
        LocalDateTime creationTimestamp = (LocalDateTime) verificationData.get("timestamp");
        if (Duration.between(creationTimestamp, now).toMinutes() >= EXPIRATION_MINUTES) {
            session.removeAttribute(sessionKey); // 만료 시 세션 데이터 삭제
            handleFailedAttempt(sessionId, now); // 실패 처리
            return ResponseEntity.badRequest().body(Map.of("message", "인증 시간이 만료되었습니다."));
        }

        // 4. 코드 일치 확인
        String storedCode = (String) verificationData.get("code");
        if (storedCode != null && storedCode.equals(inputCode)) {
            session.removeAttribute(sessionKey); // 성공 시 세션 데이터 삭제
            failedAttempts.remove(sessionId); // 성공 시 실패 기록 초기화
            session.setAttribute("PhoneAuthTime", LocalDateTime.now());
            return ResponseEntity.ok(Map.of("message", "인증 성공"));
        } else {
            handleFailedAttempt(sessionId, now); // 실패 처리
            return ResponseEntity.badRequest().body(Map.of("message", "인증번호가 일치하지 않습니다."));
        }
    }

    // 인증 실패 처리 로직
    private void handleFailedAttempt(String sessionId, LocalDateTime now) {
        AttemptInfo attemptInfo = failedAttempts.computeIfAbsent(sessionId, k -> new AttemptInfo());

        // 이미 잠긴 상태면 아무것도 안 함 (메시지는 컨트롤러에서 처리)
        if (attemptInfo.isLocked(now)) {
            return;
        }

        // 5분 이상 지났으면 실패 기록 초기화
        if (attemptInfo.getFirstFailTime() == null ||
                Duration.between(attemptInfo.getFirstFailTime(), now).toMinutes() >= FAIL_WINDOW_MINUTES) {
            attemptInfo.resetFailCount(now);
        } else {
            attemptInfo.incrementFailCount();
        }

        // 실패 횟수가 최대치에 도달했으면 잠금 처리
        if (attemptInfo.getFailCount() >= MAX_FAIL_ATTEMPTS) {
            attemptInfo.lock(now, LOCKOUT_MINUTES);
            System.out.println("세션 잠김: " + sessionId + " (10분간)");
        }

        // 변경된 정보 저장 (computeIfAbsent로 가져왔으므로 다시 put 할 필요 없음)
        // failedAttempts.put(sessionId, attemptInfo); // ConcurrentHashMap에서는 필요 없음
    }

    // 인증 시도 정보 저장 클래스 (내부 클래스 또는 별도 파일)
    @Getter
    @Setter // 편의상 Setter 추가 (혹은 생성자/메서드로 상태 변경)
    private static class AttemptInfo {
        private int failCount = 0;
        private LocalDateTime firstFailTime = null; // 실패 창 시작 시간
        private LocalDateTime lockedUntil = null; // 잠금 만료 시간

        public void incrementFailCount() {
            this.failCount++;
            if (this.firstFailTime == null) { // 첫 실패 시 시간 기록
                this.firstFailTime = LocalDateTime.now();
            }
        }

        public void resetFailCount(LocalDateTime now) {
            this.failCount = 1; // 실패했으므로 카운트는 1로 시작
            this.firstFailTime = now;
            this.lockedUntil = null; // 잠금 해제
        }

        public void lock(LocalDateTime now, int lockoutMinutes) {
            this.lockedUntil = now.plusMinutes(lockoutMinutes);
        }

        public boolean isLocked(LocalDateTime now) {
            return this.lockedUntil != null && now.isBefore(this.lockedUntil);
        }
    }
}