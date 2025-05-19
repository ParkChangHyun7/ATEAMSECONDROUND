package seoul.its.info.common.util;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ClientIpGetHelper {
    private static final Logger log = LoggerFactory.getLogger(ClientIpGetHelper.class);
    public String getClientIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // X-Forwarded-For 헤더는 여러 IP 주소를 포함할 수 있으므로 첫 번째 IP만 사용
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim(); // 공백 제거 추가
        }
        // ipv4 형식(점 3개, 4덩어리)이 아니면 111.111.111.111 반환 및 로그
        if (ip == null || ip.isEmpty() || ip.chars().filter(c -> c == '.').count() != 3) {
            log.info("ipv4가 아니라 기본값 반환됨, 실제 수집된 ip : {}", ip);
            return "111.111.111.111";
        }
        return ip;
    }
}
