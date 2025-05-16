package seoul.its.info.common.exception;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class SystemErrorResponseDto extends ErrorResponseDto {
    private String traceId;
    private String systemCode;

    public SystemErrorResponseDto(String code, String message, LocalDateTime timestamp, String path, String systemCode) {
        super(code, message, timestamp, path);
        this.traceId = UUID.randomUUID().toString(); // 고유 식별용 ID를 랜덤UUID의 문자열로 생성함.
        this.systemCode = systemCode;
    }
} 