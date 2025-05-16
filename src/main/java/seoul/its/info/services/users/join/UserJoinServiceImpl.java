package seoul.its.info.services.users.join;

import seoul.its.info.common.security.PasswordEncoder;
import seoul.its.info.common.security.aesencryptor.AESEncoderDecoder;
import seoul.its.info.services.users.validation.UserValidationService;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import jakarta.servlet.http.HttpSession;
import java.time.Duration;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserJoinServiceImpl implements UserJoinService {

    private final UserJoinMapper userJoinMapper;
    private final PasswordEncoder passwordEncoder;
    private final AESEncoderDecoder aesEncoderDecoder;
    private final UserValidationService userValidationService;

    @Override
    @Transactional
    public Map<String, Object> joinConfirm(UserJoinDto userJoinDto, HttpSession session) {
        log.info("회원가입 요청 수신: 사용자 ID={}", userJoinDto.getLogin_id());

        if (!userJoinDto.isAgreement_age() || !userJoinDto.isAgreement_service()
                || !userJoinDto.isAgreement_privacy()) {
            log.warn("필수 약관 미동의: 사용자 ID={}", userJoinDto.getLogin_id());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "비정상 접근: 필수 약관 미동의");
            return errorResponse;
        }

        Object authTimeObject = session.getAttribute("PhoneAuthTime");
        LocalDateTime phoneAuthTime = null;
        if (authTimeObject instanceof LocalDateTime) {
            phoneAuthTime = (LocalDateTime) authTimeObject;
        }

        LocalDateTime now = LocalDateTime.now();
        Duration tenMinutes = Duration.ofMinutes(10);

        if (phoneAuthTime == null || Duration.between(phoneAuthTime, now).compareTo(tenMinutes) > 0) {
            log.warn("휴대폰 인증 시간 세션 정보 없거나 만료됨: 사용자 ID={}", userJoinDto.getLogin_id());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "알 수 없는 에러입니다. 휴대폰 인증을 다시 진행해주세요.");
            return errorResponse;
        }

        userJoinDto.setPhone_verified(true);
        userJoinDto.setPhone_verified_at(phoneAuthTime);
        log.debug("휴대폰 인증 시간 확인 및 DTO 설정 완료: 사용자 ID={}, 인증 시간={}", userJoinDto.getLogin_id(), phoneAuthTime);

        log.info("회원가입 최종 검증 시작: 사용자 ID={}", userJoinDto.getLogin_id());

        Map<String, Object> validationResult = userValidationService.lastChecker(userJoinDto);

        if ("false".equals(validationResult.get("finally"))) {
            log.warn("회원가입 최종 검증 실패: 사용자 ID={}, 실패 사유={}", userJoinDto.getLogin_id(), validationResult);
            return validationResult;
        } else {
            log.info("회원가입 최종 검증 성공. 암호화 및 DB 저장 진행: 사용자 ID={}", userJoinDto.getLogin_id());

            try {
                String rawPassword = userJoinDto.getPassword();
                if (rawPassword != null && !rawPassword.isEmpty()) {
                    String encodedPassword = passwordEncoder.encode(rawPassword);
                    userJoinDto.setPassword(encodedPassword);
                    log.debug("비밀번호 암호화 완료: 사용자 ID={}", userJoinDto.getLogin_id());
                } else {
                    log.error("암호화 대상 비밀번호가 없습니다. 사용자 ID={}", userJoinDto.getLogin_id());
                    Map<String, Object> errorResponse = new HashMap<>();
                    errorResponse.put("success", false);
                    errorResponse.put("message", "필수 정보(비밀번호)가 누락되어 처리 중 오류가 발생했습니다.");
                    return errorResponse;
                }

                String rawPhoneNum = userJoinDto.getPhone_number();
                if (rawPhoneNum != null && !rawPhoneNum.isEmpty()) {
                    String encodedPhoneNum = aesEncoderDecoder.encode(rawPhoneNum);
                    userJoinDto.setPhone_number(encodedPhoneNum);
                    log.debug("휴대폰 번호 암호화 완료: 사용자 ID={}", userJoinDto.getLogin_id());
                } else {
                    log.error("암호화 대상 휴대폰 번호가 없습니다. 사용자 ID={}", userJoinDto.getLogin_id());
                    Map<String, Object> errorResponse = new HashMap<>();
                    errorResponse.put("success", false);
                    errorResponse.put("message", "필수 정보(휴대폰 번호)가 누락되어 처리 중 오류가 발생했습니다.");
                    return errorResponse;
                }

                String birthValue = userJoinDto.getBirth();
                if (birthValue != null && birthValue.trim().isEmpty()) {
                    userJoinDto.setBirth(null);
                }

                userJoinMapper.userJoinConfirm(userJoinDto);
                log.info("회원 정보 DB 저장 완료: 사용자 ID={}", userJoinDto.getLogin_id());

                Map<String, Object> successResponse = new HashMap<>();
                successResponse.put("success", true);
                successResponse.put("message", "회원가입이 성공적으로 완료되었습니다.");
                return successResponse;

            } catch (Exception e) {
                log.error("회원가입 처리 중 오류 발생: 사용자 ID={}, 오류={}", userJoinDto.getLogin_id(), e.getMessage(), e);
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "회원가입 처리 중 오류가 발생했습니다. 관리자에게 문의하세요.");
                return errorResponse;
            }
        }
    }
}
