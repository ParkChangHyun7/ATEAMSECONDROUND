package seoul.its.info.common.exception;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {
   // 이 클래스는 Dto처럼 보일 수 있지만 RuntimeException을 상속받아
   // RuntimeException의 메서드들을 사용할 수 있기 때문에
   // 단순 정보를 저장하는 Dto와 다르게 예외 처리 메서드를 호출할 수 있음.
   // 그러므로 Dto라는 이름이 사용되지 않았음.
    private final String code;
    private String field;
    private Object rejectedValue;

    public BusinessException(String code, String message) {
        super(message);
        this.code = code;
    }

    public BusinessException(String code, String message, String field, Object rejectedValue) {
        super(message);
        this.code = code;
        this.field = field;
        this.rejectedValue = rejectedValue;
    }
} 