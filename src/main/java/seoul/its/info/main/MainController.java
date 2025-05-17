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

	@Value("${vworld.api.key}")
	private String vworldApiKey;

	@Value("${open.api.base.key}")
	private String openApiKey;

	@GetMapping("/")
	public String index(@ModelAttribute("message") String message,Model model) {
		model.addAttribute("vworldApiKey", vworldApiKey);
		model.addAttribute("openApiKey", openApiKey);
		model.addAttribute("pageTitle", "메인");
		model.addAttribute("contentPage", "content_pages/index.jsp");
		model.addAttribute("resourcesPage", "include/index/resources.jsp");
		model.addAttribute("scriptsPage", "include/index/scripts.jsp");
		model.addAttribute("message", message);
		return "base";
	}

	@GetMapping("/manage")
	public String manage(Model model) {
		model.addAttribute("pageTitle", "게시판 관리");
		model.addAttribute("contentPage", "content_pages/boards/boards.jsp");
		model.addAttribute("scriptsPage", "include/boards/scripts.jsp");
			model.addAttribute("resourcesPage", "include/boards/resources.jsp");
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