package seoul.its.info.common.security.useranomaly;

import io.github.bucket4j.Bucket;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import seoul.its.info.common.exception.TooManyRequestsException;
import seoul.its.info.services.users.login.detail.UserDetailsImpl;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
@Slf4j
public class RateLimitingInterceptor implements HandlerInterceptor {

   private final RateLimitProperties rateLimitProperties;
   private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

   private static final int ADMIN_ROLE_THRESHOLD = 100;

   @Override
   public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
         throws Exception {

      if (!rateLimitProperties.isEnabled()) {
         return true;
      }

      String requestKey = resolveRequestKey(request);
      if (requestKey == null) {
         log.warn("요청 제한 키를 결정할 수 없습니다. URI: {}", request.getRequestURI());
         return true;
      }

      if (isAdmin(SecurityContextHolder.getContext().getAuthentication())) {
         log.debug("관리자 요청으로 Rate Limit 검사 건너뛰기. Key: {}, URI: {}", requestKey, request.getRequestURI());
         return true;
      }

      Bucket requestBucket = buckets.computeIfAbsent(requestKey, key -> createNewBucket(request, key));

      if (requestBucket.tryConsume(1)) {
         log.trace("Rate Limit 통과. Key: {}, URI: {}", requestKey, request.getRequestURI());
         return true;
      } else {
         log.warn("Rate Limit 초과! Key: {}, URI: {}", requestKey, request.getRequestURI());
         throw new TooManyRequestsException("페이지 요청 횟수가 초과되었습니다. 잠시 후 다시 시도해주세요.");
      }
   }

   private String resolveRequestKey(HttpServletRequest request) {
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      if (authentication != null && authentication.isAuthenticated() && !(authentication.getPrincipal() instanceof String && "anonymousUser".equals(authentication.getPrincipal()))) {
         Object principal = authentication.getPrincipal();
         if (principal instanceof UserDetailsImpl) {
            UserDetailsImpl userDetails = (UserDetailsImpl) principal;
            return "user-" + userDetails.getUsername();
         }

         return "authuser-" + authentication.getName(); 
      }

      HttpSession session = request.getSession(true);
      return "session-" + session.getId();
   }

   private boolean isAdmin(Authentication authentication) {
      if (authentication == null || !authentication.isAuthenticated()) {
         return false;
      }

      boolean isAnonymous = authentication.getAuthorities().stream()
                              .anyMatch(auth -> "ROLE_ANONYMOUS".equals(auth.getAuthority()));
      if (isAnonymous) {
         return false;
      }
      
      return authentication.getAuthorities().stream()
            .anyMatch(grantedAuthority -> {
               String authorityString = grantedAuthority.getAuthority();
               if (authorityString != null && authorityString.matches("\\d+")) {
                   try {
                      int roleValue = Integer.parseInt(authorityString);
                      return roleValue >= ADMIN_ROLE_THRESHOLD;
                   } catch (NumberFormatException e) {
                      log.error("관리자 권한 숫자 변환 중 예기치 않은 오류 발생: {}", authorityString, e);
                      return false;
                   }
               }
               return false; 
            });
   }

   private Bucket createNewBucket(HttpServletRequest request, String key) {
      RateLimitProperties.LimitConfig limitConfig = resolveLimitConfig(request, key.startsWith("user-"));
      log.debug("새로운 Bucket 생성. Key: {}, Limit: {}/{}min", key, limitConfig.getLimitForPeriod(),
            limitConfig.getPeriodMinutes());

      return Bucket.builder()
            .addLimit(limit -> limit
                  .capacity(limitConfig.getLimitForPeriod())
                  .refillIntervally(limitConfig.getLimitForPeriod(),
                        Duration.ofMinutes(limitConfig.getPeriodMinutes())))
            .build();
   }

   private RateLimitProperties.LimitConfig resolveLimitConfig(HttpServletRequest request, boolean isAuthenticated) {
      String path = request.getRequestURI();
      String method = request.getMethod().toLowerCase();

      if (rateLimitProperties.getEndpoints().containsKey(path)) {
         Map<String, RateLimitProperties.EndpointLimit> methodLimits = rateLimitProperties.getEndpoints().get(path);
         if (methodLimits.containsKey(method)) {
            RateLimitProperties.EndpointLimit endpointLimit = methodLimits.get(method);
            if (endpointLimit.isAuthenticatedOnly() && !isAuthenticated) {
               log.debug("인증 필요한 엔드포인트 접근(비로그인) - 익명 사용자 기본 설정 적용. Path: {}, Method: {}", path, method);
               return rateLimitProperties.getAnonymous();
            }
            log.debug("엔드포인트 특정 설정 적용. Path: {}, Method: {}", path, method);
            return endpointLimit;
         }
      }

      if (isAuthenticated) {
         log.debug("인증 사용자 기본 설정 적용. Path: {}, Method: {}", path, method);
         return rateLimitProperties.getAuthenticated();
      } else {
         log.debug("익명 사용자 기본 설정 적용. Path: {}, Method: {}", path, method);
         return rateLimitProperties.getAnonymous();
      }

   }
}