package seoul.its.info.common.exception;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class BusinessErrorResponseDto extends ErrorResponseDto {
   private String field;
   private Object rejectedValue;

   public BusinessErrorResponseDto(String code, String message, LocalDateTime timestamp, String path, String field,
         Object rejectedValue) {
      super(code, message, timestamp, path);
      // super는 부모 클래스에 생성된 변수를 사용할 때 쓰임.
      // 이 클래스는 ErrorResponseDto를 상속받았기 때문에
      // ErrorResponseDto의 변수를 사용할 수 있음.
      this.field = field;
      this.rejectedValue = rejectedValue;
   }
}