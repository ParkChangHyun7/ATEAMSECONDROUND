/*
package seoul.its.info.services.traffic.trafficFlow;

import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

@Controller
public class TrafficFlowMapController {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @GetMapping("/traffic/trafficflowmap")
    public String showTrafficFlowMap(Model model) {
        model.addAttribute("contentPage", "content_pages/traffic/trafficFlowMap.jsp");
        model.addAttribute("resourcesPage", "include/traffic/trafficflowmap/resources.jsp");
        model.addAttribute("scriptsPage", "include/traffic/trafficflowmap/scripts.jsp");
        return "base"; // base.jsp 템플릿
    }

    @GetMapping("/api/trafficflowmap_geojson")
    @ResponseBody
    public Map<String, Object> getTrafficFlowGeoJson() throws IOException {
        // GeoJSON 파일 로드
        Path filePath = Paths.get("src/main/resources/static/data/trafficflowmap_links2.geojson");
        Map<String, Object> geoJson = objectMapper.readValue(Files.readString(filePath), Map.class);

        // ITS API에서 교통 데이터 가져오기
        String apiUrl = "https://openapi.its.go.kr:9443/trafficInfo?apiKey=9a5ffdb76417458d88b7d2add9348307&type=all&drcType=all&minX=126.7&maxX=127.2&minY=37.4&maxY=37.7&getType=json";
        Map<String, Object> trafficData = restTemplate.getForObject(apiUrl, Map.class);

        // GeoJSON과 교통 데이터 결합 (예: 링크 ID로 매핑)
        if (trafficData != null && trafficData.containsKey("body")) {
            Map<String, Object> body = (Map<String, Object>) trafficData.get("body");
            // 예: body에서 링크별 혼잡도 데이터 추출 (API 응답 구조에 따라 수정 필요)
            // GeoJSON의 features에 혼잡도 추가
            // 이 부분은 ITS API의 실제 응답 구조에 따라 달라집니다.
        }

        return geoJson;
    }
}
*/



package seoul.its.info.services.traffic.trafficFlow;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class TrafficFlowMapController {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @GetMapping("/traffic/trafficflowmap")
    public String showTrafficFlowMap(Model model) {
        model.addAttribute("contentPage", "content_pages/traffic/trafficFlowMap.jsp");
        model.addAttribute("resourcesPage", "include/traffic/trafficflowmap/resources.jsp");
        model.addAttribute("scriptsPage", "include/traffic/trafficflowmap/scripts.jsp");
        return "base";
    }

    @GetMapping("/api/trafficflowmap_geojson")
    @ResponseBody
    public Map<String, Object> getTrafficFlowGeoJson() {
        try {
            // GeoJSON 파일 로드
            Path filePath = Paths.get("src/main/resources/static/data/trafficflowmap_links2.geojson");
            String absolutePath = filePath.toAbsolutePath().toString();
            if (!Files.exists(filePath)) {
                System.err.println("GeoJSON file not found at: " + absolutePath);
                return Map.of("error", "GeoJSON file not found at: " + absolutePath);
            }
            String fileContent = Files.readString(filePath);
            System.out.println("GeoJSON file content (first 100 chars): " + fileContent.substring(0, Math.min(100, fileContent.length()))); // 디버깅용
            Map<String, Object> geoJson = objectMapper.readValue(fileContent, Map.class);

            // ITS API 데이터 가져오기
            String apiUrl = "https://openapi.its.go.kr:9443/trafficInfo?apiKey=9a5ffdb76417458d88b7d2add9348307&type=all&drcType=all&minX=126.7&maxX=127.2&minY=37.4&maxY=37.7&getType=json";
            Map<String, Object> trafficData = null;
            try {
                trafficData = restTemplate.getForObject(apiUrl, Map.class);
                System.out.println("ITS API response: " + trafficData);
            } catch (RestClientException e) {
                System.err.println("Failed to fetch ITS API data: " + e.getMessage());
            }

            // ITS API 데이터를 GeoJSON에 매핑
            List<Map<String, Object>> features = (List<Map<String, Object>>) geoJson.get("features");
            if (features == null || features.isEmpty()) {
                System.err.println("No features found in GeoJSON");
                return Map.of("error", "No features found in GeoJSON");
            }

            if (trafficData != null && trafficData.containsKey("body")) {
                Map<String, Object> body = (Map<String, Object>) trafficData.get("body");
                if (body != null && body.containsKey("items")) {
                    List<Map<String, Object>> items = (List<Map<String, Object>>) body.get("items");
                    if (items != null) {
                        Map<String, Double> speedMap = new HashMap<>();
                        for (Map<String, Object> item : items) {
                            String linkId = (String) item.get("linkId");
                            Object speedObj = item.get("speed");
                            Double speed = speedObj instanceof Number ? ((Number) speedObj).doubleValue() : 40.0;
                            speedMap.put(linkId, speed);
                        }

                        for (Map<String, Object> feature : features) {
                            Map<String, Object> properties = (Map<String, Object>) feature.get("properties");
                            String linkId = (String) properties.get("LINK_ID");
                            properties.put("speed", speedMap.getOrDefault(linkId, 40.0));
                        }
                    } else {
                        for (Map<String, Object> feature : features) {
                            Map<String, Object> properties = (Map<String, Object>) feature.get("properties");
                            properties.put("speed", 40.0);
                        }
                    }
                } else {
                    for (Map<String, Object> feature : features) {
                        Map<String, Object> properties = (Map<String, Object>) feature.get("properties");
                        properties.put("speed", 40.0);
                    }
                }
            } else {
                for (Map<String, Object> feature : features) {
                    Map<String, Object> properties = (Map<String, Object>) feature.get("properties");
                    properties.put("speed", 40.0);
                }
            }

            return geoJson;
        } catch (IOException e) {
            System.err.println("Error reading GeoJSON file: " + e.getMessage());
            return Map.of("error", "Failed to load GeoJSON file: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            return Map.of("error", "Unexpected error: " + e.getMessage());
        }
    }
}
