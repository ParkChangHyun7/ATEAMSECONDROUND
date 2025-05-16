
package seoul.its.info.services.users.login.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import seoul.its.info.services.users.login.UserLoginMapper;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service  // Spring Bean으로 등록
@RequiredArgsConstructor  // 생성자 주입 자동 생성
@Slf4j  //  로그 출력용 어노테이션
public class LoginAttemptServiceImpl implements LoginAttemptService {
	// DB 접근을 위한 매퍼 (로그인 실패 카운트 등 업데이트)
    private final UserLoginMapper userLoginMapper;
    //  사용자 기준 최대 로그인 실패 허용 횟수 (기본값 5)
    @Value("${user.login.max-fail-count:5}")
    private int maxUserFailCount;
    // 출처(IP) 기준 최대 로그인 실패 허용 횟수 (기본값 10)
    @Value("${user.login.unknown-source.max-fail-count:10}")
    private int maxUnknownSourceFailCount;
    // 출처(IP)별 로그인 실패 횟수 기록 Map (스레드 안전성 확보)
    private final Map<String, AtomicInteger> unknownSourceAttempts = new ConcurrentHashMap<>();
    // 로그인 시도 가능 여부 확인 메서드
    @Override
    public boolean isAttemptAllowed(HttpServletRequest request, String userId) {
        String sourceIdentifier = getRequestSourceIdentifier(request);
       // IP 기준 시도 제한 초과 여부 확인
        if (!isAttemptAllowedForUnknownSource(sourceIdentifier)) {
            log.warn("로그인 시도 차단 (출처 기반): Source={}, Attempts={}", sourceIdentifier,
                    unknownSourceAttempts.get(sourceIdentifier).get());
            return false;
        }
         // 사용자 기준 시도 제한 초과 여부 확인
        if (userId != null) {
            if (isUserAccountLocked(userId, sourceIdentifier)) {
                return false;
            }
        }

        return true;
    }
    //  로그인 실패 시 기록 처리
    @Override
    public void recordFailure(HttpServletRequest request, String userId) {
        String sourceIdentifier = getRequestSourceIdentifier(request);
        // 출처(IP) 기준 실패 카운트 증가
        recordFailForUnknownSource(sourceIdentifier);
        // 사용자 기준 실패 카운트 증가
        if (userId != null) {
            Map<String, Object> user = userLoginMapper.findUserById(userId);
            if (user != null) {
                incrementFailCountForUser(userId);
            } else {
                log.warn("recordFailure 호출 시 userId({})가 제공되었으나 DB에 사용자가 없어 DB 카운트를 증가시키지 않음. Source={}", userId,
                        sourceIdentifier);
            }
        }
    }
     //  로그인 성공 시 실패 기록 초기화 처리
    @Override
    public void resetAttempts(HttpServletRequest request, String userId) {
        String sourceIdentifier = getRequestSourceIdentifier(request);

        resetSuccessForUser(userId);  // DB 실패 횟수 초기화 및 로그인 시간 갱신

        clearAttemptsForSource(sourceIdentifier);  // IP 기준 실패 기록 제거
    }
    //  요청한 사용자의 IP 주소를 식별 (로컬호스트 처리 포함)
    private String getRequestSourceIdentifier(HttpServletRequest request) {
        String ip = request.getRemoteAddr();
        if ("0:0:0:0:0:0:0:1".equals(ip)) {
            ip = "127.0.0.1";
        }
        return ip;
    }
    // DB에서 사용자 실패 카운트를 확인하고 차단 여부 반환
    private boolean isUserAccountLocked(String userId, String sourceIdentifier) {
        Map<String, Object> user = userLoginMapper.findUserById(userId);
        if (user != null) {
            int userFailCount = (Integer) user.getOrDefault("login_fail_count", 0);
            if (userFailCount >= maxUserFailCount) {
                log.warn("로그인 시도 차단 (사용자 기반): ID={}, Source={}, Attempts={}", userId, sourceIdentifier, userFailCount);
                return true;
            }
        }
        return false;
    }
    //  DB에 사용자 실패 횟수 증가 요청
    private void incrementFailCountForUser(String userId) {
        try {
            userLoginMapper.updateLoginFailCount(userId);
        } catch (Exception e) {
            log.error("DB 로그인 실패 카운트 증가 중 오류 발생: ID={}, Error={}", userId, e.getMessage(), e);
        }
    }
    //  로그인 성공 시 DB 기록 초기화 및 마지막 로그인 시간 갱신
    private void resetSuccessForUser(String userId) {
        try {
            userLoginMapper.resetLoginFailCount(userId);
            userLoginMapper.updateLastLoginAt(userId);
        } catch (Exception e) {
            log.error("DB 로그인 성공 처리 중 오류 발생: ID={}, Error={}", userId, e.getMessage(), e);
        }
    }
    //  IP 기준 로그인 시도 허용 여부 판단 (허용 초과 시 차단)
    private boolean isAttemptAllowedForUnknownSource(String sourceIdentifier) {
        AtomicInteger attempts = unknownSourceAttempts.get(sourceIdentifier);
        if (attempts != null && attempts.get() >= maxUnknownSourceFailCount) {
            log.warn("로그인 시도 차단 (출처 기반): Source={}, Attempts={}", sourceIdentifier, attempts.get());
            return false;
        }
        return true;
    }
    // 실패 시도 누적 (출처 기준)
    private void recordFailForUnknownSource(String sourceIdentifier) {
        int currentAttempts = unknownSourceAttempts.computeIfAbsent(sourceIdentifier, k -> new AtomicInteger(0))
                .incrementAndGet();
        log.debug("출처 기반 실패 기록: Source={}, Attempts={}", sourceIdentifier, currentAttempts);
    }
    // 로그인 성공 시 출처 기준 실패 기록 삭제
    private void clearAttemptsForSource(String sourceIdentifier) {
        AtomicInteger removed = unknownSourceAttempts.remove(sourceIdentifier);
        if (removed != null) {
            log.debug("출처 기반 임시 시도 횟수 삭제: Source={}, PreviousAttempts={}", sourceIdentifier, removed.get());
        }
    }
}