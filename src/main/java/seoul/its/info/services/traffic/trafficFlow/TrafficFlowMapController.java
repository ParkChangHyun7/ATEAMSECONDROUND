package seoul.its.info.services.traffic.trafficFlow;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class TrafficFlowMapController {

	@GetMapping("/traffic/trafficFlowMap")
	public String showTrafficFlowMap(Model model) {
	    model.addAttribute("pageTitle", "교통 소통 지도");
	    model.addAttribute("contentPage", "content_pages/traffic/trafficflowmap.jsp");
	    model.addAttribute("resourcesPage", "include/traffic/trafficflowmap/resources.jsp");
	    model.addAttribute("scriptsPage", "include/traffic/trafficflowmap/scripts.jsp");
	    return "base";
	}

//	// API (json 반환)
//	@GetMapping("/api/traffic/trafficFlowMap")
//	@ResponseBody
//	public List<TrafficFlowDto> getTrafficFlowData() {
//	    // 서비스 사용 시:
//	    // return trafficFlowService.getTrafficFlowData();
//
//	    // 서비스 없이 직접 구현 (예시):
//	    // ITS API 호출 → JSON 파싱 → List<TrafficFlowDto> 반환
//	    List<TrafficFlowDto> result = new ArrayList<>();
//	    // 여기서 ITS API 호출 및 파싱 구현 필요
//	    return result;
//	}

	
	
}
