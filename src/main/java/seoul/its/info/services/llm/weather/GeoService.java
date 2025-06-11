package seoul.its.info.services.llm.weather;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

@Service
public class GeoService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${kakao.api.rest.key}")
    private String kakaoRestApiKey;

    private static final String KAKAO_GEO_API_BASE_URL = "https://dapi.kakao.com/v2/local/search/address.json";

    // 기상청 격자 변환 상수
    private static final double RE = 6371.00877; // 지구 반경(km)
    private static final double GRID = 5.0; // 격자 간격(km)
    private static final double SLAT1 = 30.0; // 투영 위도1(degree)
    private static final double SLAT2 = 60.0; // 투영 위도2(degree)
    private static final double OLON = 126.0; // 기준점 경도(degree)
    private static final double OLAT = 38.0; // 기준점 위도(degree)
    private static final double XO = 43; // 기준점 X좌표(GRID)
    private static final double YO = 136; // 기준점 Y좌표(GRID)

    public GeoService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public Map<String, Integer> getCoordinatesForCity(String city) {
        try {
            String apiUrl = UriComponentsBuilder.fromUriString(KAKAO_GEO_API_BASE_URL)
                .queryParam("query", city)
                .build()
                .encode()
                .toUriString();

            org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
            headers.set("Authorization", "KakaoAK " + kakaoRestApiKey);
            org.springframework.http.HttpEntity<String> entity = new org.springframework.http.HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(
                apiUrl,
                org.springframework.http.HttpMethod.GET,
                entity,
                String.class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                JsonNode root = objectMapper.readTree(response.getBody());
                JsonNode documents = root.path("documents");

                if (documents.isArray() && documents.size() > 0) {
                    JsonNode firstDocument = documents.get(0);
                    double longitude = firstDocument.path("x").asDouble();
                    double latitude = firstDocument.path("y").asDouble();

                    // 위도, 경도를 기상청 격자 좌표로 변환
                    LatXLngY gridCoords = convertGRID_GPS(TO_GRID, latitude, longitude);

                    Map<String, Integer> result = new HashMap<>();
                    result.put("nx", (int) gridCoords.x);
                    result.put("ny", (int) gridCoords.y);
                    return result;
                }
            }
            System.out.println("카카오 지오코딩 API에서 해당 도시의 위도/경도 정보를 찾을 수 없습니다: " + city);
            return null;

        } catch (Exception e) {
            System.err.println("카카오 지오코딩 API 호출 또는 좌표 변환 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    // 기상청 격자 변환 메서드 및 DTO
    private static final int TO_GRID = 0;
    private static final int TO_GPS = 1;

    private LatXLngY convertGRID_GPS(int mode, double lat_X, double lng_Y) {
        double DEGRAD = Math.PI / 180.0;
        double RADDEG = 180.0 / Math.PI;

        double re = RE / GRID;
        double slat1 = SLAT1 * DEGRAD;
        double slat2 = SLAT2 * DEGRAD;
        double olon = OLON * DEGRAD;
        double olat = OLAT * DEGRAD;

        double sn = Math.tan(Math.PI * 0.25 + slat2 * 0.5) / Math.tan(Math.PI * 0.25 + slat1 * 0.5);
        sn = Math.log(Math.cos(slat1) / Math.cos(slat2)) / Math.log(sn);
        double sf = Math.tan(Math.PI * 0.25 + slat1 * 0.5);
        sf = Math.pow(sf, sn) * Math.cos(slat1) / sn;
        double ro = Math.tan(Math.PI * 0.25 + olat * 0.5);
        ro = re * sf / Math.pow(ro, sn);
        LatXLngY rs = new LatXLngY();

        if (mode == TO_GRID) {
            rs.lat = lat_X;
            rs.lng = lng_Y;
            double ra = Math.tan(Math.PI * 0.25 + (lat_X) * DEGRAD * 0.5);
            ra = re * sf / Math.pow(ra, sn);
            double theta = lng_Y * DEGRAD - olon;
            if (theta > Math.PI) theta -= 2.0 * Math.PI;
            if (theta < -Math.PI) theta += 2.0 * Math.PI;
            theta *= sn;
            rs.x = Math.floor(ra * Math.sin(theta) + XO + 0.5);
            rs.y = Math.floor(ro - ra * Math.cos(theta) + YO + 0.5);
        } else {
            rs.x = lat_X;
            rs.y = lng_Y;
            double xn = lat_X - XO;
            double yn = ro - lng_Y + YO;
            double ra = Math.sqrt(xn * xn + yn * yn);
            if (sn < 0.0) {
                ra = -ra;
            }
            double alat = Math.pow((re * sf / ra), (1.0 / sn));
            alat = 2.0 * Math.atan(alat) - Math.PI * 0.5;

            double theta = 0.0;
            if (Math.abs(xn) <= 0.0) {
                theta = 0.0;
            } else {
                if (Math.abs(yn) <= 0.0) {
                    theta = Math.PI * 0.5;
                    if (xn < 0.0) {
                        theta = -theta;
                    }
                }
                else theta = Math.atan2(xn, yn);
            }
            double alon = theta / sn + olon;
            rs.lat = alat * RADDEG;
            rs.lng = alon * RADDEG;
        }
        return rs;
    }

    private static class LatXLngY {
        public double lat;
        public double lng;
        public double x;
        public double y;
    }
}
