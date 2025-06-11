package seoul.its.info.services.llm.weather.api;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import seoul.its.info.services.llm.weather.dto.kma.KmaMidFcstResponse;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class KmaMidFcstApiCaller {

    private final RestTemplate restTemplate;

    @Value("${open.api.weather.key}")
    private String serviceKey;

    private static final String KMA_MID_FCST_URL = "http://apis.data.go.kr/1360000/MidFcstInfoService/getMidFcst";

    public KmaMidFcstApiCaller(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public KmaMidFcstResponse getMidFcst(String stnId) {
        LocalDateTime now = LocalDateTime.now();
        // 중기예보 발표 시각: 매일 06시와 18시
        String baseTime;
        if (now.getHour() >= 18) {
            baseTime = "1800";
        } else {
            baseTime = "0600";
        }

        String baseDate = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String tmFc = baseDate + baseTime;

        // TODO: city를 stnId로 변환하는 로직 필요. 일단 전국(108)으로 고정.
        // stnId는 기상청 중기예보 지점 번호 (예: 108: 전국, 109: 서울, 인천, 경기도)
        // 이 부분은 나중에 GeoService 또는 WeatherService에서 도시 이름에 따라 적절한 stnId를 반환하도록 수정
        String actualStnId = stnId != null && !stnId.isEmpty() ? stnId : "108"; 

        URI uri = UriComponentsBuilder.fromUriString(KMA_MID_FCST_URL)
                .queryParam("serviceKey", serviceKey)
                .queryParam("pageNo", "1")
                .queryParam("numOfRows", "10")
                .queryParam("dataType", "JSON")
                .queryParam("stnId", actualStnId)
                .queryParam("tmFc", tmFc)
                .build()
                .encode()
                .toUri();

        return restTemplate.getForObject(uri, KmaMidFcstResponse.class);
    }
} 