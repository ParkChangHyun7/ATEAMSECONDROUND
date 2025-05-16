package seoul.its.info.services.contact;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequestMapping("/contact")
@RequiredArgsConstructor
@Slf4j
public class ContactPageController {

    private final ContactPageService contactService;

    @GetMapping
    public String contactGet(Model model) {
        model.addAttribute("pageTitle", "문의하기");
        model.addAttribute("contentPage", "content_pages/contact.jsp");
        model.addAttribute("scriptsPage", "include/contact/scripts.jsp");
        model.addAttribute("resourcesPage", "include/contact/resources.jsp");
        return "base";
    }

    @PostMapping
    public String submitContactForm(@Valid ContactRequestDto contactDto,
            BindingResult bindingResult,
            @RequestParam(value = "attachment", required = false) MultipartFile attachment,
            RedirectAttributes redirectAttributes,
            HttpServletRequest httpRequest) {

        log.info("문의 접수 시도: {}", contactDto);

        if (!StringUtils.hasText(contactDto.getEmail()) && !StringUtils.hasText(contactDto.getPhone())) {
            bindingResult.reject("emailOrPhoneRequired", "이메일 또는 연락처 중 하나는 필수 입력입니다.");
            log.warn("문의 접수 유효성 검사 오류: 이메일/연락처 미입력");
        }

        if (bindingResult.hasErrors()) {
            log.warn("문의 접수 유효성 검사 오류: {}", bindingResult.getAllErrors());
            redirectAttributes.addFlashAttribute("error", "입력 내용을 확인해주세요.");
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.contactDto",
                    bindingResult);
            redirectAttributes.addFlashAttribute("contactDto", contactDto);
            return "redirect:/contact";
        }

        contactDto.setAttachment(attachment);

        try {
            contactService.saveContactInquiry(contactDto, httpRequest);
            log.info("문의 처리 서비스 호출 완료");

            redirectAttributes.addFlashAttribute("success", "문의가 성공적으로 접수되었습니다.");
            return "redirect:/contact";
        } catch (IOException e) {
            log.error("문의 처리 중 파일 관련 오류 발생", e);
            redirectAttributes.addFlashAttribute("error", "파일 처리 중 오류가 발생했습니다. 관리자에게 문의하세요.");
            return "redirect:/contact";
        } catch (Exception e) {
            log.error("문의 처리 중 예상치 못한 오류 발생", e);
            redirectAttributes.addFlashAttribute("error", "문의 처리 중 오류가 발생했습니다. 다시 시도해주세요.");
            return "redirect:/contact";
        }
    }
}
