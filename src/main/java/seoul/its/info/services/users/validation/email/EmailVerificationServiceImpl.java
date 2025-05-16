package seoul.its.info.services.users.validation.email;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import seoul.its.info.common.exception.BusinessException;
import seoul.its.info.common.exception.ErrorCode;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailVerificationServiceImpl implements EmailVerificationService {

    private final JavaMailSender javaMailSender;

    private static final String FROM_ADDRESS = "seoulitsinfo@naver.com"; // 실제 사용자 이메일 주소로 변경

    @Override
    public void sendVerificationEmail(String email, String code) throws BusinessException {
        log.info("[EmailVerification] 인증 코드 발송 시도: {}", email);

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();

        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8"); // 파일 첨부 없음, UTF-8
                                                                                                      // 인코딩
            mimeMessageHelper.setTo(email);
            mimeMessageHelper.setFrom(FROM_ADDRESS); // 발신자 설정
            mimeMessageHelper.setSubject("[Seoul ITS Info] 이메일 인증 코드입니다."); // 제목 설정

            // HTML 형식으로 이메일 본문 작성 (필요에 따라 텍스트나 템플릿 엔진 사용 가능)
            String htmlContent = String.format(
                    "<html><body>" +
                            "<h1>이메일 인증 코드</h1>" +
                            "<p>요청하신 인증 코드는 다음과 같습니다:</p>" + "<br/>" +
                            "<h2 style='color:blue;'>%s</h2>" + "<br/>" +
                            "<p>이 코드를 인증 창에 입력해주세요.</p>" +
                            "</body></html>",
                    code);
            mimeMessageHelper.setText(htmlContent, true); // true: HTML 메일

            javaMailSender.send(mimeMessage);

            // log.info("[EmailVerification] Verification code sent successfully to: {}",
            // email);
            log.info("[EmailVerification] 인증 코드 발송 성공: {}", email);

        } catch (MessagingException e) {
            // log.error("[EmailVerification] Failed to create MimeMessage for: {}", email,
            // e);
            log.error("[EmailVerification] MimeMessage 생성 실패: {}", email, e);
            // MimeMessage 생성 관련 오류 처리
            throw new BusinessException(ErrorCode.UNEXPECTED_EMAIL_ERROR.name(),
                    ErrorCode.UNEXPECTED_EMAIL_ERROR.getMessage());
        } catch (MailException e) {
            // log.error("[EmailVerification] Failed to send verification code to: {}",
            // email, e);
            log.error("[EmailVerification] 인증 코드 발송 실패: {}", email, e);
            // Spring Mail 관련 예외 처리 (연결, 인증 등)
            throw new BusinessException(ErrorCode.EMAIL_SEND_FAILURE.name(), ErrorCode.EMAIL_SEND_FAILURE.getMessage());
        } catch (Exception e) {
            // log.error("[EmailVerification] Unexpected error occurred while sending email
            // to: {}", email, e);
            log.error("[EmailVerification] 이메일 발송 중 예상치 못한 오류 발생: {}", email, e);
            // 기타 예상치 못한 예외 처리
            throw new BusinessException(ErrorCode.UNEXPECTED_EMAIL_ERROR.name(),
                    ErrorCode.UNEXPECTED_EMAIL_ERROR.getMessage());
        }
    }
}