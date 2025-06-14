package seoul.its.info.services.llm.weather;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.annotation.PostConstruct;

@Service
public class GoogleWeather {
 final String GOOGLE_API_KEY = "AIzaSyD2Zixvx0WQODkkmXjCx9XNRHHKGlASjdA";

 @PostConstruct
 public String getSeoulCurrentWeather() {
     String baseUrl = "https://weather.googleapis.com/v1/currentConditions:lookup";
     double latitude = 37.5665;
     double longitude = 126.9780;

     RestTemplate restTemplate = new RestTemplate();

     String requestUrl = UriComponentsBuilder.fromUriString(baseUrl)
             .queryParam("key", GOOGLE_API_KEY)
             .queryParam("location.latitude", latitude)
             .queryParam("location.longitude", longitude)
             .toUriString();

     try {
         String response = restTemplate.getForObject(requestUrl, String.class);
         System.out.println("서울 날씨 정보: " + response);
         return response;
     } catch (Exception e) {
         System.err.println("구글 날씨 API 호출 중 오류 발생: " + e.getMessage());
         return "날씨 정보를 가져오는 데 실패했습니다.";
     }
 }
}