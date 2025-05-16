package seoul.its.info.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j

/*
 * 공통적으로 익셉션 처리에 사용되는 클래스.
 * try catch 문으로 메서드 흐름을 제어하는 경우가 아니고
 * 예외처리 메서드를 따로 기입하지 않으면 스프링 프레임워크에서
 * 예외 처리의 가장 상위 클래스인 Exception이 연결 된
 * GlobalExceptionHandler로 죄다 빠짐.
 * 
 * 그럼, 익셉션 핸들러가 왜 필요하냐? 개발자가 개발하다가 이 부분에서는
 * 보안 에러 가능성이 있겠다 싶으면 catch 부분에 하나하나 넣을 필요 없이
 * SecurityException 클래스 호출로 익셉션 핸들러 어노테이션이 있는
 * 클래스와 맵핑된 메서드로 연결해줌.
 * 
 * throw new SecurityException("코드명", "상세 메시지") 이렇게 쓰면
 * SecurityException 클래스의 메서드 중에 매칭되는 메서드로 연결함.
 * 
 * throw new SecurityException("코드명", "상세 메시지", "보안 레벨", "필요 권한") 이렇게 쓰면
 * 생성자 오버로드 된 메서드로 연결돼서 처리함. 잘 보면 호출하는 이름이 같은데
 * 인자값이 다름. 말인즉 오버로딩 됐다는 말임.
 * 핵심은 리턴에 얼마나 많은 정보를 담겠냐에 따라서 어떻게 보낼지 결정하면 됨.
 * 
 * 리턴값은 주로 view에 전달해서 클라이언트에게 보여지는 게 목적이라...
 * 일반 유저에게 보이는 메시지라면 코드명, 상세 메시지 정도로도 충분함.
 * 
 * 그러나 보안 에러 가능성이 있는 부분이고, 관리자가 사용하는 페이지에 던지는
 * 에러 메시지라면 보안 레벨, 필요 권한 등을 추가해서 보내는 식으로 처리하면 됨.
 * 나중에 다른 인자값 등을 담아서 보내고 싶으면 생성자 오버로딩으로 추가해서
 * 처리하면 됨.
 */

@RestControllerAdvice
public class CommonExceptionHandler {
      
      // --- 필드 유효성 검사 오류 상세 정보 DTO ---
      @Getter
      @RequiredArgsConstructor
      private static class FieldErrorDetail {
            private final String field; // 오류가 발생한 필드명
            private final String message; // 오류 메시지
      }

      // --- 유효성 검사 오류 응답 DTO ---
      @Getter
      private static class ValidationErrorResponse {
            private final LocalDateTime timestamp = LocalDateTime.now();
            private final String path;
            private final List<FieldErrorDetail> errors;

            public ValidationErrorResponse(WebRequest request, List<FieldErrorDetail> errors) {
                  this.path = request.getDescription(false);
                  this.errors = errors;
            }
      }

      // 비즈니스 예외 처리로 비지니스 로직 에러를 처리할 클래스를 맵핑함.
      @ExceptionHandler(BusinessException.class)
      public ResponseEntity<BusinessErrorResponseDto> handleBusinessException(
                  BusinessException ex,
                  WebRequest request) {
            /*
             * 발생한 오류로 호출된 BusinessException 클래스의 객체 ex와
             * 웹에서 발생한 요청 정보를 받아서 핸들 메서드를 가동함.
             * WebRequest는 어디 주소에서 무슨 값 던졌더니 에러 나는지를 알기 위해서 넣어야됨.
             * ex 객체가 가지는 값은 throw로 호출할 때 주어진 값에 따라 2가지로 달라짐.
             */
            BusinessErrorResponseDto response = new BusinessErrorResponseDto(
                        // Dto의 메서드 객체인 response를 생성하고
                        // ex 객체가 가진 값을 넣어서 초기화함.
                        ex.getCode(),
                        ex.getMessage(),
                        LocalDateTime.now(),
                        request.getDescription(false),
                        ex.getField(),
                        ex.getRejectedValue());
            // 인자값 2개짜리 생성자자가 throw new로 호출 됐다면
            // getField, getRejectedValue 메서드는 null값이 들어감.
            // 최종 프론트로 전달되는 값에도 field: null 처럼 들어감
            // 프론트는 전달된 값을 모두 선택적으로 쓸 수도 있고
            // 모두 안 쓸 수도 있으니 내가 지정했는데 값이 안 들어가고
            // null이 들어간 경우를 제외하면 널 값 신경 쓸 필요 없고
            // 나머지는 프론트에 맡기면 됨.

            String sessionId = request.getSessionId() != null ? request.getSessionId() : "N/A";
            log.error("비지니스 예외 발생: SessionId={}, Path={}, Details={}", sessionId, request.getDescription(false), ex.getMessage(), ex);
            // 익셉션 발생은 모두 로그화 시키도록 익셉션 핸들러를 설정할 때는
            // 로그를 설정해주는 것이 상식!

            return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(response);
            // ResponseEntity는 클라이언트에게 응답을 보내는 객체임.
            // "400에러 났어요(BAD_REQUEST). 상세 내역은 response에 있슴다"
            // 하고 json 형식으로 카톡 쏴주는 역할을 함.
            // 나머지 익셉션 처리 메서드들도 대동소이하게 작동합니다!
      }

      // 시스템 예외 처리
      @ExceptionHandler(SystemException.class)
      public ResponseEntity<SystemErrorResponseDto> handleSystemException(
                  SystemException ex,
                  WebRequest request) {

            SystemErrorResponseDto response = new SystemErrorResponseDto(
                        ex.getCode(),
                        ex.getMessage(),
                        LocalDateTime.now(),
                        request.getDescription(false),
                        ex.getSystemCode());

            String sessionId = request.getSessionId() != null ? request.getSessionId() : "N/A";
            log.error("System Exception occurred: SessionId={}, Path={}, Details={}", sessionId, request.getDescription(false), ex.getMessage(), ex);

            return ResponseEntity
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(response);
      }

      // 보안 예외 처리
      @ExceptionHandler(SecurityException.class)
      public ResponseEntity<SecurityErrorResponseDto> handleSecurityException(
                  SecurityException ex,
                  WebRequest request) {

            SecurityErrorResponseDto response = new SecurityErrorResponseDto(
                        ex.getCode(),
                        ex.getMessage(),
                        LocalDateTime.now(),
                        request.getDescription(false),
                        ex.getSecurityLevel(),
                        ex.getRequiredRole());

            String sessionId = request.getSessionId() != null ? request.getSessionId() : "N/A";
            log.error("Security Exception occurred: SessionId={}, Path={}, Details={}", sessionId, request.getDescription(false), ex.getMessage(), ex);

            return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED) // 또는 FORBIDDEN 등 상황에 맞게
                        .body(response);
      }

      // @Valid 유효성 검사 실패 처리 핸들러 (반!드!시!!! handleGlobalException 위에 추가)
      // 이거 글로벌 밑으로 넣으면 익셉션 처리 컨트롤러가 발리데이션 오류 처리하기 전에
      // 글로벌로 에러 처리 해버림. 순서 정확히 지켜주셈
      @ExceptionHandler(MethodArgumentNotValidException.class)
      public ResponseEntity<ValidationErrorResponse> handleValidationExceptions(
                  MethodArgumentNotValidException ex,
                  WebRequest request) {

            // BindingResult에서 FieldError 목록 추출
            List<FieldErrorDetail> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                        .map(fieldError -> new FieldErrorDetail(fieldError.getField(), fieldError.getDefaultMessage()))
                        .collect(Collectors.toList());

            ValidationErrorResponse response = new ValidationErrorResponse(request, fieldErrors);

            String sessionId = request.getSessionId() != null ? request.getSessionId() : "N/A";
            log.warn("유효성 검사 오류 발생: SessionId={}, Path={}, Errors={}", sessionId, request.getDescription(false), fieldErrors);

            return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST) // 400 상태 코드와 함께
                        .body(response); // 유효성 검사 오류 때 발생하도록 설정한 message를 반환함.
            // 이건 프론트로 반환해주는 값이라 비밀번호 검증때는 "아이디 또는 비밀번호를 확인해주세요."로
            // 출력하는 원칙을 위배하기는 하는데 서버에 아이디가 있는지 같은 여부등
            // 공격자에게 필요한 정보를 넘겨주는 영역이 아니라서 세부 메시지 그대로 반환 하기로 결정함
      }

      // 파일 처리 등 입출력 관련 예외 처리
      @ExceptionHandler(IOException.class)
      public ResponseEntity<ErrorResponseDto> handleIOException(
                  IOException ex,
                  WebRequest request) {

          ErrorResponseDto response = new ErrorResponseDto(
                  "FILE_IO_ERROR",
                  "파일 처리 중 오류가 발생했습니다. 관리자에게 문의하세요.", // 사용자에게 노출될 메시지
                  LocalDateTime.now(),
                  request.getDescription(false));

          // 로그에는 구체적인 IOException 메시지 포함
          String sessionId = request.getSessionId() != null ? request.getSessionId() : "N/A";
          log.error("IOException occurred: SessionId={}, Path={}, Details={}", sessionId, request.getDescription(false), ex.getMessage(), ex);

          return ResponseEntity
                  .status(HttpStatus.INTERNAL_SERVER_ERROR) // 파일 시스템 문제 등 서버 내부 오류로 간주
                  .body(response);
      }

      @ExceptionHandler(TooManyRequestsException.class)
      public ResponseEntity<ErrorResponseDto> handleTooManyRequestsException(
                  TooManyRequestsException ex,
                  WebRequest request) {

            ErrorResponseDto response = new ErrorResponseDto(
                        "TOO_MANY_REQUESTS",
                        ex.getMessage(),
                        LocalDateTime.now(),
                        request.getDescription(false));

            String sessionId = request.getSessionId() != null ? request.getSessionId() : "N/A";
            log.warn("Rate Limit 초과: SessionId={}, Path={}, Message={}", sessionId, request.getDescription(false), ex.getMessage());

            return ResponseEntity
                        .status(HttpStatus.TOO_MANY_REQUESTS)
                        .body(response);
      }

      // 처리되지 않은 모든 예외 처리
      // Exception 클래스는 예외 처리의 최상위 클래스, 조상님이라
      // 밑에 자손놈들이 처리하지 못한 모든 예외를 처리해줌.
      // 우리가 명시적으로 "여기선 비지니스 에러, 여기선 보안 에러"
      // 이렇게 처리해주지 않는 이상 모두 다 Exception이 처리함.
      // 지금은 생각나면 명시해주고 아니면 안 적어서 global이
      // 처리하도록 냅둠..
      @ExceptionHandler(Exception.class)
      public ResponseEntity<ErrorResponseDto> handleGlobalException(
                  Exception ex,
                  WebRequest request) {

            ErrorResponseDto response = new ErrorResponseDto(
                        "INTERNAL_SERVER_ERROR",
                        "예상치 못한 오류가 발생했습니다.",
                        LocalDateTime.now(),
                        request.getDescription(false));

            String sessionId = request.getSessionId() != null ? request.getSessionId() : "N/A";
            log.error("Unhandled Exception occurred: SessionId={}, Path={}, Details={}", sessionId, request.getDescription(false), ex.getMessage(), ex);

            return ResponseEntity
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(response);
      }
}