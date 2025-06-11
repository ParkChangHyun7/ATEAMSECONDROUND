package seoul.its.info.services.llm.weather.api;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import seoul.its.info.services.llm.weather.dto.kma.KmaUltraSrtNcstResponse;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class KmaUltraSrtNcstApiCaller {

    private final RestTemplate restTemplate;

    @Value("${open.api.weather.key}")
    private String serviceKey;

    private static final String KMA_ULTRA_SRT_NCST_URL = "http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getUltraSrtNcst";

    public KmaUltraSrtNcstApiCaller(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public KmaUltraSrtNcstResponse getUltraSrtNcst(int nx, int ny) {
        LocalDateTime now = LocalDateTime.now();

        // 기상청 API는 매 시각 40분에 발표되므로, 현재 시간 기준으로 적절한 base_time 계산
        // 예: 10:30 -> base_time 1000
        // 예: 10:40 -> base_time 1000
        // 예: 10:41 -> base_time 1000 (이전 시간을 가져와야 함)
        // 예: 11:00 -> base_time 1000
        // 정시 40분 이후에 호출하면 현재 시각 -1시간으로 해야 가장 최신 데이터를 가져올 수 있음.
        // 그렇지 않으면 다음 시각 40분까지 이전 데이터를 사용함.
        // 기준시각은 현재 시각의 40분 전으로 설정함. (예: 현재 14:00 -> 13:00 기준, 현재 14:45 -> 14:00 기준)
        LocalDateTime baseDateTime;
        if (now.getMinute() < 40) {
            baseDateTime = now.minusHours(1);
        } else {
            baseDateTime = now;
        }

        String baseDate = baseDateTime.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String baseTime = baseDateTime.format(DateTimeFormatter.ofPattern("HH00")); // 시만 가져오고 분은 00으로 고정

        URI uri = UriComponentsBuilder.fromUriString(KMA_ULTRA_SRT_NCST_URL)
                .queryParam("serviceKey", serviceKey)
                .queryParam("pageNo", 1)
                .queryParam("numOfRows", 1000)
                .queryParam("dataType", "JSON")
                .queryParam("base_date", baseDate)
                .queryParam("base_time", baseTime)
                .queryParam("nx", nx)
                .queryParam("ny", ny)
                .build(true)
                .toUri();

        return restTemplate.getForObject(uri, KmaUltraSrtNcstResponse.class);
    }
} 