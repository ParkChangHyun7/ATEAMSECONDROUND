package seoul.its.info.services.traffic.trafficFlow;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Controller
public class TrafficFlowMapController {

    @GetMapping("/traffic/trafficflowmap")
    public String showTrafficFlowMap(Model model) {
        model.addAttribute("contentPage", "content_pages/traffic/trafficFlowMap.jsp");
        model.addAttribute("resourcesPage", "include/traffic/trafficflowmap/resources.jsp");
        model.addAttribute("scriptsPage", "include/traffic/trafficflowmap/scripts.jsp");

        return "base"; // base.jsp 사용
    }

    @ResponseBody
    @GetMapping("/api/traffic/trafficflowmap")
    public Map<String, Object> getTrafficFlowData() {
        String apiUrl = "https://openapi.its.go.kr:9443/trafficInfo?apiKey=9a5ffdb76417458d88b7d2add9348307&type=all&drcType=all&minX=126.7&maxX=127.2&minY=37.4&maxY=37.7&getType=json";

        RestTemplate restTemplate = new RestTemplate();
        Map<String, Object> response = restTemplate.getForObject(apiUrl, Map.class);

        return response;
    }
}

