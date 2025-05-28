package seoul.its.info.services.traffic.trafficFlow;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TrafficFlowMapController {

    @GetMapping("/traffic/trafficFlowMap")
    public String showTrafficFlowMap(Model model) {
        model.addAttribute("contentPage", "content_pages/traffic/trafficFlowMap.jsp");
        model.addAttribute("resourcesPage", "include/traffic/trafficFlowMap/resources.jsp");
        model.addAttribute("scriptsPage", "include/traffic/trafficFlowMap/scripts.jsp");
        return "base";
    }

	
	
}
