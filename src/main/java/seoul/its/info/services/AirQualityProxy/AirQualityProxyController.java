package seoul.its.info.services.AirQualityProxy;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api/air")
public class AirQualityProxyController {

    @Value("${seoul.api.key}")
    private String apiKey;

    @GetMapping
    public String getAirQuality() {
        String url = "https://openapi.seoul.go.kr:8088/" + apiKey + "/json/RealtimeCityAir/1/25/";
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(url, String.class);
    }
}
