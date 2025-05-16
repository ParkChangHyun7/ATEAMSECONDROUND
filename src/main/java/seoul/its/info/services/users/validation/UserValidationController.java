package seoul.its.info.services.users.validation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import seoul.its.info.common.exception.BusinessException;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/user/validation")
@RequiredArgsConstructor
public class UserValidationController {

    private final UserValidationService validationService;

    @PostMapping("/dupl-check")
    public ResponseEntity<?> checkDuplicate(@Validated @RequestBody ValidationRequestDto requestDto)
            throws BusinessException {

        String type = requestDto.getType();
        String value = requestDto.getValue();
        log.info("/dupl-check API 호출: type={}, value={}", type, value);

        try {
            // 모든 검증 로직을 서비스 계층의 validateSingleField 메서드에 위임
            Map<String, Object> result = validationService.validateSingleField(type, value);
            log.info("/dupl-check API 결과: {}", result);
            // 서비스 결과 맵을 그대로 응답 본문으로 사용
            return ResponseEntity.ok(result);

        } catch (BusinessException e) {
            // 서비스에서 발생한 BusinessException 처리 (INVALID_TYPE 등)
            log.error("/dupl-check 처리 중 BusinessException 발생: {}", e.getMessage());
            // CommonExceptionHandler에서 처리하도록 그대로 throw 하거나
            // 여기서 직접 오류 응답을 만들 수 있음 (현재는 throw 유지)
            throw e;
            // Map<String, Object> errorResponse = Map.of("success", false, "message",
            // e.getMessage());
            // return ResponseEntity.badRequest().body(errorResponse); // 400 Bad Request 등
            // 상태 코드 고려
        } catch (Exception e) {
            // 기타 예외 처리
            log.error("/dupl-check 처리 중 예상치 못한 오류 발생", e);
            Map<String, Object> errorResponse = Map.of("success", false, "message", "서버 내부 오류가 발생했습니다.");
            return ResponseEntity.internalServerError().body(errorResponse); // 500 Internal Server Error
        }
    }
}

// @PostMapping("/check-user_id")
// public ResponseEntity<?> checkDuplicateUserId(
// @RequestBody Map<String, String> request) {
// log.info("checkRegexUserId 호출됨: {}", request.get("user_id"));
// return ResponseEntity.ok(Map.of("success", true));
// }

// @PostMapping("/check-nickname")
// public ResponseEntity<?> checkDuplicateNickname(
// @Validated(UserJoinDto.NicknameValidation.class) @RequestBody UserJoinDtodto)
//
// {
// log.info("checkRegexNickname 호출됨: {}", dto.getNickname());
// return ResponseEntity.ok(Map.of("success", true));
// }

// @PostMapping("/check-email")
// public ResponseEntity<?> checkDuplicateEmail(
// @Validated(UserJoinDto.EmailValidation.class) @RequestBody UserJoinDto dto) {
// log.info("checkRegexEmail 호출됨: {}", dto.getEmail());
// return ResponseEntity.ok(Map.of("success", true));
// }

// @PostMapping("/check-phone")
// public ResponseEntity<?> checkDuplicatePhone(
// @Validated(UserJoinDto.PhoneValidation.class) @RequestBody UserJoinDto dto) {
// log.info("checkRegexPhone 호출됨: {}", dto.getPhone_number());
// return ResponseEntity.ok(Map.of("success", true));
// }

// @PostMapping("/check-password")
// public ResponseEntity<?> checkDuplicatePassword(
//
// @Validated(UserJoinDto.PasswordValidation.class) @RequestBody UserJoinDtodto)
// {
// log.info("checkRegexPassword 호출됨: {}", dto.getPassword());
// return ResponseEntity.ok(Map.of("success", true));
// }