package seoul.its.info.services.contact;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class ContactRequestDto {

    private Long id;

    @NotBlank(message = "이름은 필수 입력 항목입니다.")
    private String name;

    @Email(message = "유효한 이메일 형식이 아닙니다.")
    private String email;

    private String phone;

    @NotBlank(message = "제목은 필수 입력 항목입니다.")
    private String subject;

    @NotBlank(message = "문의 내용은 필수 입력 항목입니다.")
    private String message;

    private MultipartFile attachment;

    private String attachmentPath;

    private Integer fileIncluded;
}