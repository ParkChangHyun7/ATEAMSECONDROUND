package seoul.its.info.services.AirQualityProxy;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@RestController
@RequestMapping("/api/air")
public class AirQualityProxyController {

    @Value("${open.api.air.key}")
    private String apiKey;

    @GetMapping
    public ResponseEntity<Map> getAirQuality() {
        String url = "http://openapi.seoul.go.kr:8088/" + apiKey + "/json/RealtimeCityAir/1/25/";

        RestTemplate restTemplate = new RestTemplate();
        Map response = restTemplate.getForObject(url, Map.class);

        return ResponseEntity.ok(response);
    }
}
