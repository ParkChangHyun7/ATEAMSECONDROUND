// package seoul.its.info.common.config;

// import org.springframework.stereotype.Component;
// import org.springframework.web.servlet.HandlerInterceptor;

// import jakarta.servlet.http.HttpServletRequest;
// import jakarta.servlet.http.HttpServletResponse;
// import jakarta.servlet.http.HttpSession;
// import lombok.RequiredArgsConstructor;
// import lombok.extern.slf4j.Slf4j;
// import seoul.its.info.services.users.logout.LogoutService;

// @Slf4j
// @Component
// @RequiredArgsConstructor
// public class TimeoutInterceptor implements HandlerInterceptor {

//    private final LogoutService logoutService;

//    @Override
//    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
//          throws Exception {
//       log.debug("TimeoutInterceptor preHandle: Request URI = {}", request.getRequestURI());
//       HttpSession session = request.getSession(false);
      
//       if (session == null || session.getAttribute("lastLoginUserActtivityTime") == null) {
//           log.debug("세션 또는 마지막 활동 시간이 없어 타임아웃 검사 건너뜀");
//          return true;
//       }

//       long lastLoginTime = (long) session.getAttribute("lastLoginUserActtivityTime");
//       long currentTime = System.currentTimeMillis();
//       long timeoutMillis = 30 * 60 * 1000L;

//       if (currentTime - lastLoginTime > timeoutMillis) {
//          log.info("세션 타임아웃 감지: SessionId={}", session.getId());
//          logoutService.logout(request);
//          log.info("세션 타임아웃으로 로그아웃 처리 완료: SessionId={}", session.getId());
//          response.sendRedirect(request.getContextPath() + "/login?timeout=true");
//          return false;
//       } else {
//          session.setAttribute("lastLoginUserActtivityTime", currentTime);
//          log.debug("세션 유효, 마지막 활동 시간 갱신: SessionId={}", session.getId());
//          return true;
//       }
//    }
// }
