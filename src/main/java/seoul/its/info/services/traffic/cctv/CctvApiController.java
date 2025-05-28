package seoul.its.info.services.traffic.cctv;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class CctvApiController {

    private final String apiUrl = "https://openapi.its.go.kr:9443/cctvInfo?apiKey=9a5ffdb76417458d88b7d2add9348307&type=its&cctvType=1&minX=126.7&maxX=127.2&minY=37.4&maxY=37.7&getType=json";

    @GetMapping("/api/cctv/list")
    public List<Map<String, Object>> getCctvList() {
        List<Map<String, Object>> result = new ArrayList<>();
        try {
            String json = new RestTemplate().getForObject(apiUrl, String.class);
            JsonNode items = new ObjectMapper().readTree(json).path("response").path("data");

            for (JsonNode node : items) {
                Map<String, Object> cctv = new HashMap<>();
                cctv.put("coordX", node.path("coordx").asDouble());
                cctv.put("coordY", node.path("coordy").asDouble());
                cctv.put("cctvname", node.path("cctvname").asText());
                cctv.put("cctvurl", node.path("cctvurl").asText());
                result.add(cctv);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    // ❌ 아래 proxy() 메서드는 완전히 삭제!!!
}
