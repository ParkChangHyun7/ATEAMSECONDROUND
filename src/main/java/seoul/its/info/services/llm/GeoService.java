package seoul.its.info.services.llm;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.annotation.PostConstruct;

@Service
public class GeoService {

    @Value("${kakao.api.rest.key}")
    private String kakaoApiKey;

    public String getCoordinates(String neighborhoodName) {        
        RestTemplate restTemplate = new RestTemplate();

        // 1. HttpHeaders 객체 생성 및 Authorization 헤더 추가
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + kakaoApiKey);
        headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE); // JSON 응답을 받기 위함

        // 2. HttpEntity 객체 생성 (헤더 포함)
        HttpEntity<String> entity = new HttpEntity<>(headers);

        String requestUrl = UriComponentsBuilder
                .newInstance()
                .scheme("https")
                .host("dapi.kakao.com")
                .path("/v2/local/search/address.json")
                .queryParam("query", neighborhoodName)
                .build()
                .toUriString();

        // 3. exchange 메서드를 사용하여 헤더를 포함한 요청 보내기
        String response = restTemplate.exchange(requestUrl, HttpMethod.GET, entity, String.class).getBody();
        return response; // JSON 형태로 반환
    }
    
    public void checkCoordinates() {
        String neighborhoodName = "행신동";
        String coordinates = getCoordinates(neighborhoodName);
        System.out.println("Coordinates for " + neighborhoodName + ": " + coordinates);
    }
}