package seoul.its.info.services.traffic.visualize;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TrafficAccidentVisualizeController {    
    @GetMapping("/traffic/visualize/accident")
    public String about(Model model) {
        model.addAttribute("pageTitle", "교통사고 정보 시각화");
        model.addAttribute("contentPage", "content_pages/traffic/visualize/visualizeAccident.jsp");
        model.addAttribute("resourcesPage", "include/traffic/visualize/resources.jsp");
        model.addAttribute("scriptsPage", "include/traffic/visualize/scripts.jsp");
        return "base";
    }
}
