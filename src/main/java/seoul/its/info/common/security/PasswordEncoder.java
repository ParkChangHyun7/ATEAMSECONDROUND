package seoul.its.info.common.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class PasswordEncoder {
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    /**
     * 비밀번호를 BCrypt 방식으로 암호호화 하는 메서드
     * password: 암호화할 원본 비밀번호
     * return: 암호화된 비밀번호 문자열
     * 리턴된 문자열을 DB에 저장해야 함.
     */
    public String encode(String password) {
        return encoder.encode(password);
    }

    /**
     * 원본 비밀번호와 암호화된 비밀번호의 일치 여부를 확인함.
     * 내부 오류 발생 시 false를 반환하고 로그를 남김.
     * rawPassword: 사용자가 입력한 비밀번호
     * encodedPassword: DB에 저장된 암호화 비밀번호
     * return: 일치 여부 (true: 일치, false: 불일치 또는 내부 오류)
     */
    public boolean matches(String rawPassword, String encodedPassword) {
        try {
            // encodedPassword가 null이거나 유효하지 않은 형식일 때 matches가 예외를 던질 수 있음
            if (rawPassword == null || encodedPassword == null) {
                log.warn("Password matching attempted with null input. rawPassword null: {}, encodedPassword null: {}",
                        rawPassword == null, encodedPassword == null);
                return false;
            }
            return encoder.matches(rawPassword, encodedPassword);
        } catch (Exception e) {
            // 던져진 예외는 로그로 기록하고 false를 전달함.
            log.error("비밀번호 검증 중 오류 발생: {}", e.getMessage(), e);
            return false; // 예외 발생 시 무조건 false 반환
        }
    }
    // BycryptPasswordEncoder는 암호화나 검증 작업에 사용되는 클래스 내에서 임포트하고
    // encode나 matches를 호출할 수도 있지만 프로젝트 구조 일관성을 위해
    // 별도의 클래스로 분리했음. 실제 비밀번호 검증에 사용되는 클래스는 BCryptPasswordEncoder임.
    // 이 클래스는 커스텀 로그와 예외 처리를 추가하기 위해 사용되는 클래스임.
}