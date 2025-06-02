package seoul.its.info.services.traffic.trafficFlow;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Controller // ⭐️⭐️⭐️ 반드시 Controller 사용해야 JSP 반환 가능!
public class TrafficFlowMapController {

    @GetMapping("/traffic/trafficflowmap")
    public String showTrafficFlowMap() {
        return "content_pages/traffic/trafficFlowMap"; // 경로는 대문자/소문자 맞춰서 사용 OK
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
