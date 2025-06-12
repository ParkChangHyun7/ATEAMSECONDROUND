package seoul.its.info.services.AirQualityProxy;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@RestController
@RequestMapping("/api/air")
public class AirQualityProxyController {

    @GetMapping
    public ResponseEntity<Map> getAirQuality() {
        // 발급받은 키 중 아무거나 사용 가능
        String apiKey = "466b566d6f636861343879484c5773";
        String url = "http://openapi.seoul.go.kr:8088/" + apiKey + "/json/RealtimeCityAir/1/25/";

        RestTemplate restTemplate = new RestTemplate();
        Map result = restTemplate.getForObject(url, Map.class);

        return ResponseEntity.ok(result);
    }
}
