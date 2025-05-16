package seoul.its.info.common.exception;

import lombok.Getter;

@Getter
public class SecurityException extends RuntimeException {
    private final String code;
    private String securityLevel;
    private String requiredRole;

    public SecurityException(String code, String message) {
        super(message);
        this.code = code;
    }

    public SecurityException(String code, String message, String securityLevel, String requiredRole) {
        super(message);
        this.code = code;
        this.securityLevel = securityLevel;
        this.requiredRole = requiredRole;
    }
} 