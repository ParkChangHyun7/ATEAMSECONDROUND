package seoul.its.info.main;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class MainController {

	@Value("${kakao.api.js.key}")
	private String kakaoApiKey;

	@GetMapping("/")
	public String index(@ModelAttribute("message") String message,Model model) {
		model.addAttribute("kakaoApiKey", kakaoApiKey);
		model.addAttribute("pageTitle", "메인");
		model.addAttribute("contentPage", "content_pages/index.jsp");
		model.addAttribute("resourcesPage", "include/index/resources.jsp");
		model.addAttribute("scriptsPage", "include/index/scripts.jsp");
		model.addAttribute("message", message);
		return "base";
	}

	@GetMapping("/admin")
	public String adminPage(Model model) {
		model.addAttribute("pageTitle", "관리자 페이지");
		model.addAttribute("contentPage", "content_pages/admin/whereto.jsp");
		model.addAttribute("scriptsPage", "include/admin/scripts.jsp");
		model.addAttribute("resourcesPage", "include/admin/resources.jsp");
		return "base";
	  }

	@GetMapping("/about")
	public String about(Model model) {
		model.addAttribute("pageTitle", "소개");
		model.addAttribute("contentPage", "content_pages/about.jsp");
		model.addAttribute("resourcesPage", "include/about/resources.jsp");
		model.addAttribute("scriptsPage", "include/about/scripts.jsp");
		return "base";
	}
}