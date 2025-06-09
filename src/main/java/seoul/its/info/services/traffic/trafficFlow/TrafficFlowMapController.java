package seoul.its.info.services.traffic.trafficFlow;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TrafficFlowMapController {

	
	

	
    @GetMapping("/traffic/trafficflowmap")
    public String showTrafficFlowMap(Model model) {
        model.addAttribute("pageTitle", "교통흐름지도");
        model.addAttribute("contentPage", "content_pages/traffic/trafficFlowMap.jsp");
        return "base";  // base.jsp 템플릿 사용
    }
}

