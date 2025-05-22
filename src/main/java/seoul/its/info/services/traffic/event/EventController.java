package seoul.its.info.services.traffic.event;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/traffic")
public class EventController {

    @GetMapping("/eventMap")
    public String eventMap(Model model) {
        model.addAttribute("pageTitle", "돌발상황 지도");
        model.addAttribute("contentPage", "content_pages/traffic/eventMap.jsp");
        model.addAttribute("resourcesPage", "include/traffic/eventMap/resources.jsp");
        model.addAttribute("scriptsPage", "include/traffic/eventMap/scripts.jsp");
        return "base";
    }
}
