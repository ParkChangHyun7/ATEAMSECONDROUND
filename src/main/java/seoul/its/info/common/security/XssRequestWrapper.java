package seoul.its.info.common.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import org.apache.commons.text.StringEscapeUtils;

/**
 * XSS 공격 방지를 위한 HTTP 요청 래퍼 클래스
 * 
 * 주요 기능:
 * 1. 요청 파라미터의 XSS 공격 가능성이 있는 문자열 제거
 * 2. HTML 태그 이스케이프 처리
 */
public class XssRequestWrapper extends HttpServletRequestWrapper {
    // 일단 요즘 브라우저들은 웬만해선 자체적으로 Xss 필터링 기능이
    // 탑재되어 있어서 Xss 필터 클래스 중요성은 떨어지는 추세..라고 함.
    public XssRequestWrapper(HttpServletRequest request) {
        super(request);
    }

    @Override
    public String getParameter(String parameter) {
        String value = super.getParameter(parameter);
        return value == null ? null : StringEscapeUtils.escapeHtml4(value);
    }

    @Override
    public String[] getParameterValues(String parameter) {
        String[] values = super.getParameterValues(parameter);
        if (values == null) {
            return null;
        }
        int count = values.length;
        String[] encodedValues = new String[count];
        for (int i = 0; i < count; i++) {
            encodedValues[i] = StringEscapeUtils.escapeHtml4(values[i]);
        }
        return encodedValues;
    }
}