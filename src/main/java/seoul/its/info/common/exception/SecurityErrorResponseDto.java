package seoul.its.info.common.exception;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class SecurityErrorResponseDto extends ErrorResponseDto {
    private String securityLevel; // 보안 레벨
    private String requiredRole;  // 필요한 권한

    public SecurityErrorResponseDto(String code, String message, LocalDateTime timestamp, String path, String securityLevel, String requiredRole) {
        super(code, message, timestamp, path);
        this.securityLevel = securityLevel;
        this.requiredRole = requiredRole;
    }
} 