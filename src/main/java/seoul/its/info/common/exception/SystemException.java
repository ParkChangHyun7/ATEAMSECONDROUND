package seoul.its.info.common.exception;

import lombok.Getter;

@Getter
public class SystemException extends RuntimeException {
    private final String code;
    private String systemCode;

    public SystemException(String code, String message) {
        super(message);
        this.code = code;
    }

    public SystemException(String code, String message, String systemCode) {
        super(message);
        this.code = code;
        this.systemCode = systemCode;
    }
} 