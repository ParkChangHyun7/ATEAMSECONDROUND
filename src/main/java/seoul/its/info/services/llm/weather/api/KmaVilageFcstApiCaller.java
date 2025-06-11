package seoul.its.info.services.llm.weather.api;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import seoul.its.info.services.llm.weather.dto.kma.KmaVilageFcstResponse;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class KmaVilageFcstApiCaller {

    private final RestTemplate restTemplate;

    @Value("${open.api.weather.key}")
    private String serviceKey;

    private static final String KMA_VILAGE_FCST_URL = "http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getVilageFcst";

    public KmaVilageFcstApiCaller(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public KmaVilageFcstResponse getVilageFcst(int nx, int ny) {
        LocalDateTime now = LocalDateTime.now();
        // 발표 시간 계산: 기상청 단기예보 API는 2시, 5시, 8시 등 3시간 간격으로 발표되며, 각 시간 10분 후에 발표 됨
        // 현재 시간에서 가장 가까운 과거 발표 시각을 탐색
        int hour = now.getHour();
        String baseTime;
        if (hour >= 23) { // 23시 이후는 다음날 02시 데이터 기준
            baseTime = "2300"; // 전날 마지막 발표 시간 기준으로 처리
            now = now.minusDays(1); // 날짜도 하루 전으로
        } else if (hour >= 20) {
            baseTime = "2000";
        } else if (hour >= 17) {
            baseTime = "1700";
        } else if (hour >= 14) {
            baseTime = "1400";
        } else if (hour >= 11) {
            baseTime = "1100";
        } else if (hour >= 8) {
            baseTime = "0800";
        } else if (hour >= 5) {
            baseTime = "0500";
        } else if (hour >= 2) {
            baseTime = "0200";
        } else { // 00시, 01시
            baseTime = "2300"; // 전날 23시 기준
            now = now.minusDays(1); // 날짜도 하루 전으로
        }

        String baseDate = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        URI uri = UriComponentsBuilder.fromUriString(KMA_VILAGE_FCST_URL)
                .queryParam("serviceKey", serviceKey)
                .queryParam("pageNo", "1")
                .queryParam("numOfRows", "1000") // 충분히 많은 데이터를 가져오도록 설정
                .queryParam("dataType", "JSON")
                .queryParam("base_date", baseDate)
                .queryParam("base_time", baseTime)
                .queryParam("nx", nx)
                .queryParam("ny", ny)
                .build()
                .encode()
                .toUri();

        return restTemplate.getForObject(uri, KmaVilageFcstResponse.class);
    }
} 