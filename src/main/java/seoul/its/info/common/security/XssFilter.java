package seoul.its.info.common.security;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import org.springframework.stereotype.Component;

@Component
public class XssFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // 필터 초기화 시 필요한 작업이 있다면 여기에 구현
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        // HttpServletRequest를 XssRequestWrapper로 감싸서 전달함.
        chain.doFilter(new XssRequestWrapper((HttpServletRequest) request), response);
    }

    @Override
    public void destroy() {
        // 필터 종료 시 필요한 작업이 있다면 여기에 구현
    }
}