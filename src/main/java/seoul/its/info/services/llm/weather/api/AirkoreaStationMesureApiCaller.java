package seoul.its.info.services.llm.weather.api;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import seoul.its.info.services.llm.weather.dto.airkorea.AirkoreaStationMesureResponse;

import java.net.URI;

@Component
public class AirkoreaStationMesureApiCaller {

    private final RestTemplate restTemplate;

    @Value("${open.api.weather.key}") // 통합 키 사용
    private String serviceKey;

    private static final String AIRKOREA_STATION_MESURE_URL = "http://apis.data.go.kr/B552584/ArpltnInforInqireSvc/getMsrstnAcctoRltmMesureDnsty";

    public AirkoreaStationMesureApiCaller(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public AirkoreaStationMesureResponse getStationMesure(String stationName) {
        URI uri = UriComponentsBuilder.fromUriString(AIRKOREA_STATION_MESURE_URL)
                .queryParam("serviceKey", serviceKey)
                .queryParam("returnType", "json")
                .queryParam("numOfRows", "1")
                .queryParam("pageNo", "1")
                .queryParam("stationName", stationName)
                .encode()
                .build()
                .toUri();

        return restTemplate.getForObject(uri, AirkoreaStationMesureResponse.class);
    }
} 